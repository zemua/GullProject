package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.dto.WrapAtributosForCampoDto;
import lombok.Data;
import reactor.core.publisher.Mono;

@Data
@Service
public class PropuestaUtilities {
	
	ConsultaService consultaService;
	
	@Autowired
	public PropuestaUtilities(ConsultaService consultaService) {
		this.consultaService = consultaService;
	}

	public Mono<WrapAtributosForCampoDto> wrapAtributos(String propuestaId) { // TODO test
		return consultaService.findAttributesByPropuestaId(propuestaId)
				.collectList()
				.map(rList -> {
					WrapAtributosForCampoDto dto = new WrapAtributosForCampoDto();
					dto.setAtributos(rList);
					return dto;
				});
	}
	
	public Mono<List<AtributoForCampo>> atributosFromWrap(WrapAtributosForCampoDto wrapAtributosForCampoDto, BindingResult bindingResult, String propuestaId) { // TODO test
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
	
}
