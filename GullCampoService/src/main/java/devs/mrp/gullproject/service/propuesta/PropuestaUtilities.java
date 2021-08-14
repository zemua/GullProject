package devs.mrp.gullproject.service.propuesta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.TipoPropuesta;
import devs.mrp.gullproject.domainsdto.linea.WrapAtributosForCampoDto;
import devs.mrp.gullproject.domainsdto.propuesta.AtributoForFormDto;
import devs.mrp.gullproject.domainsdto.propuesta.AttributesListDto;
import devs.mrp.gullproject.domainsdto.propuesta.ProposalPie;
import devs.mrp.gullproject.domainsdto.propuesta.WrapPropuestaClienteAndSelectableAttributes;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.WrapPropuestaNuestraAndSelectableAttributes;
import devs.mrp.gullproject.domainsdto.propuesta.proveedor.WrapPropuestaProveedorAndSelectableAttributes;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.AtributoUtilities;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Data
@Service
public class PropuestaUtilities {
	
	ConsultaService consultaService;
	AtributoServiceProxyWebClient atributoService;
	ModelMapper modelMapper;
	AtributoUtilities atributoUtilities;
	@Autowired LineaService lineaService;
	
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
	
	public List<AtributoForCampo> listOfSelectedAttributesPlain(List<AtributoForFormDto> atts){
		List<AtributoForCampo> attributes = new ArrayList<>();
		if (atts == null || atts.size() == 0) {
			return attributes;
		} else {
			attributes = atts.stream()
					.filter(a -> a.getSelected())
					.map(a -> modelMapper.map(a, AtributoForCampo.class))
					.collect(Collectors.toList());
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
	
	/**
	 * PROPUESTA CLIENTE
	 * @param propuesta
	 * @return
	 */
	
	public Mono<WrapPropuestaClienteAndSelectableAttributes> wrapPropuestaClienteWithAlAvailableAttributesAsSelectable(PropuestaCliente propuesta) {
		return atributoUtilities.getAttributesAsSelectable()
				.collectList().map(atts -> {
					var wrap = new WrapPropuestaClienteAndSelectableAttributes();
					wrap.setPropuestaCliente(propuesta);
					wrap.setAttributes(atts);
					return wrap;
				});
	}
	
	public PropuestaCliente getPropuestaClienteFromWrapWithSelectableAttributes(WrapPropuestaClienteAndSelectableAttributes wrap) {
		PropuestaCliente propuesta = wrap.getPropuestaCliente();
		propuesta.setAttributeColumns(listOfSelectedAttributesPlain(wrap.getAttributes()));
		return propuesta;
	}
	
	/**
	 * PROPUESTA PROVEEDOR
	 */
	
	public Mono<List<AtributoForFormDto>> setSelectedSameAsCustomerOne(List<AtributoForFormDto> atributos, String propuestaClienteId) {
		return consultaService.findPropuestaByPropuestaId(propuestaClienteId)
			.map(prop -> {
				var operations = prop.operations();
				atributos.forEach(att -> {
					if (operations.ifHasAttributeColumn(att.getId())) {
						att.setSelected(true);
					} else {
						att.setSelected(false);
					}
				});
				return atributos;
			})
			;
	}
	
	public Mono<WrapPropuestaProveedorAndSelectableAttributes> wrapPropuestaProveedorWithAllAvailableAttributesAsSelectable(PropuestaProveedor propuesta) {
		return atributoUtilities.getAttributesAsSelectable()
				.collectList().flatMap(atts -> {
					return setSelectedSameAsCustomerOne(atts, propuesta.getForProposalId())
						.map(attSelected -> {
							var wrap = new WrapPropuestaProveedorAndSelectableAttributes();
							wrap.setPropuestaProveedor(propuesta);
							wrap.setAttributes(attSelected);
							return wrap;
						})
						;
				});
	}
	
	public PropuestaProveedor getPropuestaProveedorFromWrapWithSelectableAttributes(WrapPropuestaProveedorAndSelectableAttributes wrap) {
		PropuestaProveedor propuesta = wrap.getPropuestaProveedor();
		propuesta.setAttributeColumns(listOfSelectedAttributesPlain(wrap.getAttributes()));
		return propuesta;
	}
	
	/**
	 * PROPUESTA NUESTRA
	 */
	
	public Mono<WrapPropuestaNuestraAndSelectableAttributes> wrapPropuestaNuestraAndSelectableAttributes(PropuestaNuestra propuesta) {
		return atributoUtilities.getAttributesAsSelectable()
				.collectList().flatMap(atts -> {
					return setSelectedSameAsCustomerOne(atts, propuesta.getForProposalId())
							.map(attSelected -> {
								var wrap = new WrapPropuestaNuestraAndSelectableAttributes();
								wrap.setPropuestaNuestra(propuesta);
								wrap.setAttributes(attSelected);
								return wrap;
							})
							;
				})
				;
	}
	
	public PropuestaNuestra getPropuestaNuestraFromWrapWithSelectableAttributes(WrapPropuestaNuestraAndSelectableAttributes wrap) {
		PropuestaNuestra propuesta = wrap.getPropuestaNuestra();
		propuesta.setAttributeColumns(listOfSelectedAttributesPlain(wrap.getAttributes()));
		return propuesta;
	}
	
	/*
	 * Wrap and Organize propuestas
	 */
	
	private List<PropuestaCliente> extractPropuestasCliente(List<Propuesta> propuestas) {
		List<PropuestaCliente> list =  propuestas.stream()
			.filter(p->p.getTipoPropuesta().equals(TipoPropuesta.CLIENTE))
			.map(pr -> new PropuestaCliente(pr))
			.collect(Collectors.toList());
		list.sort((p1, p2) -> Long.valueOf(p1.getCreatedTime()).compareTo(p2.getCreatedTime()));
		return list;
	}
	
	private List<PropuestaProveedor> extractPropuestasProveedorForThisPropuestaCustomer(List<Propuesta> propuestas, String idPropuestaCliente) {
		List<PropuestaProveedor> list = propuestas.stream()
				.filter(p-> p.getTipoPropuesta().equals(TipoPropuesta.PROVEEDOR) && p.getForProposalId().equals(idPropuestaCliente))
				.map(pr -> {
					var propp = new PropuestaProveedor(pr);
					log.debug("propuesta proveedor " + propp.toString());
					log.debug("lineas asignadas " + propp.getLineasAsignadas());
					return propp;
				})
				.collect(Collectors.toList());
		list.sort((p1, p2) -> Long.valueOf(p1.getCreatedTime()).compareTo(p2.getCreatedTime()));
		return list;
	}
	private List<PropuestaNuestra> extractPropuestasNuestrasForThisPropuestaCustomer(List<Propuesta> propuestas, String idPropuestaCliente) {
		List<PropuestaNuestra> list = propuestas.stream()
				.filter(p -> p.getTipoPropuesta().equals(TipoPropuesta.NUESTRA) && p.getForProposalId().equals(idPropuestaCliente))
				.map(pr -> new PropuestaNuestra(pr))
				.collect(Collectors.toList());
		list.sort((p1, p2) -> Long.valueOf(p1.getCreatedTime()).compareTo(p2.getCreatedTime()));
		return list;
	}
	
	private Flux<Integer> setAssignedMap(ProposalPie pie) { // TODO test
		log.debug("to set the assigned map");
		var propuestas = pie.getPropuestasProveedores();
		var map = pie.getAssignedLinesOfProp();
		log.debug("propuestas " + propuestas.toString());
		return Flux.fromIterable(propuestas)
			.flatMap(prop -> {
				log.debug("for propuesta " + prop.toString());
				return lineaService.findByPropuestaId(prop.getId())
						.collectList()
						.map(rLineList -> {
							log.debug("to count assigned");
							var counter = new AtomicInteger();
							rLineList.stream().forEach(l -> {
								if (l.getCounterLineId() != null) {
									counter.addAndGet(l.getCounterLineId().size());
								}
							});
							log.debug("to put into map prop " + prop.getId() + " counter " + counter.get());
							map.put(prop.getId(), counter.get());
							return counter.get();
						});
			})
			;
	}
	
	public Flux<ProposalPie> getProposalPieFeast(String consultaId) {
		return consultaService.findAllPropuestasOfConsulta(consultaId)
			.collectList().flatMapMany(rList -> {
				List<ProposalPie> proposalPieFeast = new ArrayList<>();
				
				// Add proposals from custumers
				List<PropuestaCliente> proCli = extractPropuestasCliente(rList);
				proCli.stream().forEach(sProCli -> {
					ProposalPie pie = new ProposalPie();
					pie.setPropuestaCliente(sProCli);
					proposalPieFeast.add(pie);
				});
				
				// Add related proposals
				return Flux.fromIterable(proposalPieFeast).flatMap(pie -> {
					// from suppliers
					pie.setPropuestasProveedores(extractPropuestasProveedorForThisPropuestaCustomer(rList, pie.getPropuestaCliente().getId()));
					// and us
					pie.setPropuestasNuestras(extractPropuestasNuestrasForThisPropuestaCustomer(rList, pie.getPropuestaCliente().getId()));
					// set number of assigned lines
					return setAssignedMap(pie);
				})
				.thenMany(Flux.fromIterable(proposalPieFeast));
			})
			;
	}
	
}
