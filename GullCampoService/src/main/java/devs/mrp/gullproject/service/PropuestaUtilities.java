package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AttributesListDto;
import devs.mrp.gullproject.domains.dto.WrapAtributosForCampoDto;
import devs.mrp.gullproject.domains.dto.WrapPropuestaAndSelectableAttributes;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Service
public class PropuestaUtilities {
	
	ConsultaService consultaService;
	AtributoServiceProxyWebClient atributoService;
	ModelMapper modelMapper;
	AtributoUtilities atributoUtilities;
	
	@Autowired
	public PropuestaUtilities(ConsultaService consultaService, AtributoServiceProxyWebClient atributoService, ModelMapper modelMapper, AtributoUtilities atributoUtilities) {
		this.consultaService = consultaService;
		this.atributoService = atributoService;
		this.modelMapper = modelMapper;
		this.atributoUtilities = atributoUtilities;
	}

	public Mono<WrapAtributosForCampoDto> wrapAtributos(String propuestaId) {
		return consultaService.findAttributesByPropuestaId(propuestaId)
				.collectList()
				.map(rList -> {
					WrapAtributosForCampoDto dto = new WrapAtributosForCampoDto();
					dto.setAtributos(rList);
					return dto;
				});
	}
	
	public Mono<List<AtributoForCampo>> atributosOrderFromWrapAndValidateBelongsToPropuesta(WrapAtributosForCampoDto wrapAtributosForCampoDto, BindingResult bindingResult, String propuestaId) {
		return consultaService.findAttributesByPropuestaId(propuestaId).index()
				.collectMap((a)->a.getT2().getLocalIdentifier(),(a)->a)
				.map(rMap -> {
					List<AtributoForCampo> list = new ArrayList<>();
					wrapAtributosForCampoDto.getAtributos().stream().forEach(at -> {
						if (rMap.containsKey(at.getLocalIdentifier())) {
							AtributoForCampo afc = rMap.get(at.getLocalIdentifier()).getT2();
							afc.setOrder(at.getOrder());
							list.add(afc);
						} else {
							bindingResult.rejectValue("atributos", "error.atributos");
						}
					});
					return list;
				});
	}
	
	public Mono<AttributesListDto> getAttributesAndMarkActualFromProposal(String proposalId) {
		return consultaService.findPropuestaByPropuestaId(proposalId)
				.flatMapMany(rPropuesta -> Flux.fromIterable(rPropuesta.getAttributeColumns()))
				.map(rAtt -> rAtt.getId()).collectList()
				.flatMap(rAttList -> {
					return atributoService.getAllAtributos()
							.map(rAttProp -> modelMapper.map(rAttProp, AtributoForFormDto.class))
							.map(rAttForm -> {
								if (rAttList.contains(rAttForm.getId())) {
									rAttForm.setSelected(true);
								} else {rAttForm.setSelected(false);}
								return rAttForm;
								})
							.collectList().flatMap(rAttForm -> Mono.just(new AttributesListDto(rAttForm)));
				});
	}
	
	public Flux<AtributoForCampo> listOfSelectedAttributes(AttributesListDto atts){
		Flux<AtributoForCampo> attributes;
		if (atts.getAttributes() == null || atts.getAttributes().size() == 0) {
			attributes = Flux.fromIterable(new ArrayList<AtributoForCampo>());
		} else {
			attributes = Flux.fromIterable(atts.getAttributes())
					.filter(a -> a.getSelected())
					.map(a -> modelMapper.map(a, AtributoForCampo.class));
		}
		return attributes;
	}
	
	public Mono<Propuesta> addAllAvailableAttributes(Propuesta propuesta) {
		return atributoService.getAllAtributos()
				.collectList().map(atts -> {
					propuesta.setAttributeColumns(atts);
					return propuesta;
				});
	}
	
	public Mono<WrapPropuestaAndSelectableAttributes> wrapPropuestaWithAlAvailableAttributesAsSelectable(Propuesta propuesta) {
		return atributoUtilities.getAttributesAsSelectable()
				.collectList().map(atts -> {
					var wrap = new WrapPropuestaAndSelectableAttributes();
					wrap.setPropuesta(propuesta);
					wrap.setAttributes(atts);
					return wrap;
				});
	}
	
}
