package devs.mrp.gullproject.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.ConsultaFactory;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.domains.propuestas.PvperSum;
import devs.mrp.gullproject.domainsdto.linea.WrapAtributosForCampoDto;
import devs.mrp.gullproject.domainsdto.propuesta.AtributoForFormDto;
import devs.mrp.gullproject.domainsdto.propuesta.AttributesListDto;
import devs.mrp.gullproject.domainsdto.propuesta.ConsultaPropuestaBorrables;
import devs.mrp.gullproject.domainsdto.propuesta.ProposalPie;
import devs.mrp.gullproject.domainsdto.propuesta.WrapPropuestaClienteAndSelectableAttributes;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckboxedCostToPvper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckboxedCosts;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperSumCheckboxWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperSumCheckboxedPvpsWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperSumsOrdenablesWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvpsCheckboxWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvpsCheckboxedCostWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvpsOrdenablesWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvpsWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.WrapPropuestaNuestraAndSelectableAttributes;
import devs.mrp.gullproject.domainsdto.propuesta.proveedor.CostesCheckboxWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.proveedor.CostesOrdenablesWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.proveedor.CostesWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.proveedor.WrapPropuestaProveedorAndSelectableAttributes;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.CompoundedConsultaLineaService;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.propuesta.PropuestaUtilities;
import devs.mrp.gullproject.service.propuesta.oferta.PropuestaNuestraOperations;
import devs.mrp.gullproject.service.propuesta.proveedor.CotizacionOfCostMapperFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.PropuestaProveedorOperations;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping(path = "/consultas")
public class ConsultaController {
	
	// TODO create our proposals from customer and supplier ones (highlight cells if cost changes, until new margin is confirmed)
	// TODO adapt old supplier proposals for updated customer inquiry
	// TODO make a "compare" view to check supplier vs customer table and ours vs customer table
	// TODO add a "shared cost" for packaging for example, will have a cost for the proposal (not line) and will be shared by weight according to other selected costs, can be specified in absolute value or in percentage of the other costs
	// TODO set currency of each proposal, and make auto-conversion, and auto-fetch exchange rates from internet
	
	ConsultaService consultaService;
	LineaService lineaService;
	AtributoServiceProxyWebClient atributoService;
	PropuestaUtilities propuestaUtilities;
	ModelMapper modelMapper;
	CompoundedConsultaLineaService compoundedService;
	ConsultaFactory consultaFactory;
	
	@Autowired PvperCheckboxedCostToPvper pvperCheckboxedCostToPvper;
	@Autowired CotizacionOfCostMapperFactory costToCotiz;
	
	@Autowired
	public ConsultaController(ConsultaService consultaService, LineaService lineaService, AtributoServiceProxyWebClient atributoService, PropuestaUtilities propuestaUtilities, ModelMapper modelMapper, CompoundedConsultaLineaService compoundedService, ConsultaFactory consultaFactory) {
		this.consultaService = consultaService;
		this.lineaService = lineaService;
		this.atributoService = atributoService;
		this.propuestaUtilities = propuestaUtilities;
		this.modelMapper = modelMapper;
		this.compoundedService = compoundedService;
		this.consultaFactory = consultaFactory;
	}
	
	@GetMapping("/nuevo")
	public String createConsulta(Model model) {
		model.addAttribute("consulta", consultaFactory.create());
		return "createConsulta";
	}
	
	@PostMapping(path = "/nuevo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String processNewConsulta(@Valid Consulta consulta, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			log.debug("processNewConsulta invalid consulta received by post");
			model.addAttribute("consulta", consulta);
			return "createConsulta";
		}
		
		Mono<Consulta> c = consultaService.save(consulta);
		model.addAttribute("consulta", c);
		
		return "processNewConsulta";
	}
	
	@GetMapping("/all")
	public String showAllConsultas(Model model) {
		Flux<Consulta> consultas = consultaService.findAll();
		model.addAttribute("consultas", new ReactiveDataDriverContextVariable(consultas, 1));
		return "showAllConsultas";
	}
	
	@GetMapping("/revisar/id/{id}")
	public String reviewConsultaById(Model model, @PathVariable(name = "id") String id) {
		Mono<Consulta> consulta = consultaService.findById(id);
		model.addAttribute("consulta", consulta);
		Flux<ProposalPie> proposalPieFeast = propuestaUtilities.getProposalPieFeast(id);
		model.addAttribute("proposalPieFeast", new ReactiveDataDriverContextVariable(proposalPieFeast, 1));
		return "reviewConsulta";
	}
	
	@GetMapping("/revisar/id/{id}/edit")
	public String editConsultaDetails(Model model, @PathVariable(name = "id") String id) {
		Mono<Consulta> consulta = consultaService.findById(id);
		model.addAttribute("consulta", consulta);
		return "reviewConsultaEdit";
	}
	
	@PostMapping("/revisar/id/{id}/edit")
	public String processEditConsultaDetails(@Valid Consulta consulta, BindingResult bindingResult, Model model, @PathVariable(name = "id") String id) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("consulta", consulta);
			return "reviewConsultaEdit";
		}
		Mono<Consulta> nConsulta = consultaService.updateNameAndStatus(consulta.getId(), consulta.getNombre(), consulta.getStatus());
		model.addAttribute("consulta", nConsulta);
		return "processReviewConsultaEdit";
	}
	
	@GetMapping("/revisar/id/{id}/addpropuesta")
	public String addPropuestaToId(Model model, @PathVariable(name= "id") String id) {
		model.addAttribute("consultaId", id);
		Mono<WrapPropuestaClienteAndSelectableAttributes> propuesta = propuestaUtilities.wrapPropuestaClienteWithAlAvailableAttributesAsSelectable(new PropuestaCliente(id));
		model.addAttribute("wrapPropuestaClienteAndSelectableAttributes", propuesta);
		return "addPropuestaToConsulta";
	}
	
	@PostMapping("/revisar/id/{id}")
	public String processAddPropuestaToId(@Valid WrapPropuestaClienteAndSelectableAttributes wrapPropuestaClienteAndSelectableAttributes, BindingResult bindingResult, Model model, @PathVariable(name ="id") String id) {
		log.debug(wrapPropuestaClienteAndSelectableAttributes.toString());
		if (bindingResult.hasErrors()) {
			log.debug("processAddPropuestaToId -> invalid PropuestaCliente received by POST");
			log.debug(bindingResult.toString());
			model.addAttribute("wrapPropuestaClienteAndSelectableAttributes", wrapPropuestaClienteAndSelectableAttributes);
			model.addAttribute("consultaId", id);
			return "addPropuestaToConsulta";
		}
		Propuesta propuesta = propuestaUtilities.getPropuestaClienteFromWrapWithSelectableAttributes(wrapPropuestaClienteAndSelectableAttributes);
		propuesta.setCreatedTime(System.currentTimeMillis());
		Mono<Propuesta> c = consultaService.addPropuesta(id, propuesta).flatMap(entity -> Mono.just(entity.operations().getPropuestaByIndex(entity.operations().getCantidadPropuestas()-1)));
		model.addAttribute("propuesta", c);
		model.addAttribute("consultaId", id);
		
		return "processAddPropuestaToConsulta";
	}
	
	@GetMapping("/delete/id/{id}")
	public String deleteConsultaById(Model model, @PathVariable(name= "id") String id) {
		Mono<Consulta> c = consultaService.findById(id);
		model.addAttribute("consulta", c);
		return "deleteConsultaById";
	}
	
	@PostMapping("/delete/id/{id}")
	public String processDeleteConsultaById(Consulta consulta, Model model, @PathVariable(name ="id") String id) {
		log.debug("processDeleteConsultaById, idConsulta = " + consulta.getId());
		log.debug("processDeleteConsultaById, id = " + id);
		
		Mono<Long> c;
		Mono<Long> remLineas;
		Mono<Integer> numPropuestas;
		if (consulta.getId().equals(id)) {
			log.debug("idConsulta equals id");
			Mono<Consulta> cons = consultaService.findById(consulta.getId());
			
			c = cons.then(consultaService.deleteById(consulta.getId()));
			
			remLineas = cons.flatMap(cc -> lineaService.deleteSeveralLineasFromSeveralPropuestas(cc.getPropuestas()));
			numPropuestas = cons.flatMap(cc -> Mono.just(cc.operations().getCantidadPropuestas()));
		} else {
			log.debug("idConsulta does not equal id");
			c = Mono.empty();
			remLineas = Mono.empty();
			numPropuestas = Mono.empty();
		}
		model.addAttribute("count", c);
		model.addAttribute("numlineas", remLineas);
		model.addAttribute("numpropuestas", numPropuestas);
		model.addAttribute("consultaId", consulta.getId());
		
		return "processDeleteConsultaById";
	}
	
	@GetMapping("/delete/id/{consultaid}/propuesta/{propuestaid}")
	public String deletePropuestaById(Model model, @PathVariable(name = "consultaid") String consultaid, @PathVariable(name = "propuestaid") String propuestaid) {
		model.addAttribute("idConsulta", consultaid);
		model.addAttribute("idPropuesta", propuestaid);
		Mono<Propuesta> p = consultaService.findById(consultaid).flatMap(cons -> Mono.just(cons.operations().getPropuestaById(propuestaid)));
		model.addAttribute("propuesta", p);
		return "deletePropuestaById";
	}
	
	@PostMapping("/delete/id/{consultaid}/propuesta/{propuestaid}")
	public Mono<String> processDeletePropuestaById(ConsultaPropuestaBorrables data, Model model) {
		log.debug("id consulta: " + data.getIdConsulta());
		log.debug("id propuesta: " + data.getIdPropuesta());
		Mono<Long> lineas = lineaService.deleteSeveralLineasFromPropuestaId(data.getIdPropuesta());
		Mono<Integer> c = consultaService.removePropuestaById(data.getIdConsulta(), data.getIdPropuesta());
		model.addAttribute("idConsulta", data.getIdConsulta());
		return compoundedService.removePropuestasAssignedToAndTheirLines(data.getIdConsulta(), data.getIdPropuesta())
				.then(lineas).map(rLineas -> {
			model.addAttribute("lineasBorradas", rLineas);
			return true;
		})
			.then(c).map(rConsulta -> {
				model.addAttribute("propuestasBorradas", rConsulta);
				return true;
			})
			.then(Mono.just("processDeletePropuestaById"));
	}
	
	@GetMapping("/attof/propid/{id}")
	public String showAttributesOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		Flux<AtributoForCampo> afc = consultaService.findAttributesByPropuestaId(proposalId);
		model.addAttribute("attributes", new ReactiveDataDriverContextVariable(afc, 1));
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(proposalId);
		model.addAttribute("consulta", consulta);
		return "showAttributesOfProposal";
	}
	
	@GetMapping("/attof/propid/{id}/new")
	public String addAttributeToProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("proposalId", proposalId);
		Mono<AttributesListDto> atributos = propuestaUtilities.getAttributesAndMarkActualFromProposal(proposalId);
		model.addAttribute("atts", atributos);
		return "addAttributeToProposal";
	}
	
	@PostMapping("/attof/propid/{id}/new")
	public String processAddAttributeToProposal(@ModelAttribute AttributesListDto atts, BindingResult bindingResult, Model model, @PathVariable(name = "id") String propuestaId) {
		Flux<AtributoForCampo> attributes = propuestaUtilities.listOfSelectedAttributes(atts);
		Mono<Consulta> consulta = attributes.collectList()
				.flatMap(latts -> consultaService.updateAttributesOfPropuesta(propuestaId, latts));
		Mono<Propuesta> propuesta = consulta.map(c -> c.operations().getPropuestaById(propuestaId));
		model.addAttribute("atributos", new ReactiveDataDriverContextVariable(attributes, 1));
		model.addAttribute("propuesta", propuesta);
		return "processAddAttributeToProposal";
	}
	
	@GetMapping("/attof/propid/{id}/order")
	public String orderAttributesOfProposal(Model model, @PathVariable(name = "id") String propuestaId) {
		Mono<WrapAtributosForCampoDto> wrap = propuestaUtilities.wrapAtributos(propuestaId);
		model.addAttribute("wrapAtributosForCampoDto", wrap);
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		model.addAttribute("propuesta", propuesta);
		return "orderAttributesOfProposal";
	}
	
	@PostMapping("/attof/propid/{id}/order")
	public Mono<String> processOrderAttributesOfProposal(WrapAtributosForCampoDto wrapAtributosForCampoDto, BindingResult bindingResult, Model model, @PathVariable(name = "id") String propuestaId) {
		return propuestaUtilities.atributosOrderFromWrapAndValidateBelongsToPropuesta(wrapAtributosForCampoDto, bindingResult, propuestaId)
			.flatMap(array -> {
				return consultaService.reOrderAttributesOfPropuesta(propuestaId, array);
			})
			.map(prop -> {
				model.addAttribute("propuestaId", propuestaId);
				if (bindingResult.hasErrors()) {
					model.addAttribute("mensaje", "Error");
					return "processOrderAttributesOfProposal";
				} else {
					model.addAttribute("mensaje", "Orden guardado");
					return "processOrderAttributesOfProposal";
				}
			})
			;
	}
	
	@GetMapping("/editar/propcli/{propid}")
	public String editarProposalCliente(Model model, @PathVariable(name = "propid") String propuestaId) {
		// Beware, it is currently used also for supplier objects, because it doesn't change functionality as the "type" is not altered
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("propuestaCliente", propuesta);
		model.addAttribute("consulta", consulta);
		return "editPropuesta";
	}
	
	@PostMapping("/editar/propcli/{propid}")
	public String processEditarProposalCliente(@Valid PropuestaCliente propuestaCliente, BindingResult bindingResult, Model model, @PathVariable(name ="propid") String propuestaId) {
		// Beware, it is currently used also for supplier objects, because it doesn't change functionality as the "type" is not altered
		if (bindingResult.hasErrors()) {
			model.addAttribute("propuestaCliente", propuestaCliente);
			Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
			model.addAttribute("consulta", consulta);
			return "editPropuesta";
		}
		Mono<Consulta> cons = consultaService.updateNombrePropuesta(propuestaCliente);
		model.addAttribute("consulta", cons);
		return "processEditPropuesta";
	}
	
	/**
	 * ****************************
	 * SUPPLIER PROPOSALS
	 * ****************************
	 */
	
	@GetMapping("/revisar/id/{consultaId}/onprop/{propuestaClienteId}/addcotizacionproveedor")
	public String addProposalProveedorToProposalCliente(Model model, @PathVariable(name = "consultaId") String consultaId, @PathVariable(name = "propuestaClienteId") String propuestaClienteId) {
		model.addAttribute("consultaId", consultaId);
		Mono<WrapPropuestaProveedorAndSelectableAttributes> propuesta = propuestaUtilities.wrapPropuestaProveedorWithAllAvailableAttributesAsSelectable(new PropuestaProveedor(propuestaClienteId));
		model.addAttribute("wrapPropuestaProveedorAndSelectableAttributes", propuesta);
		return "addPropuestaProveedorToConsulta";
	}
	
	@PostMapping("/revisar/id/{consultaId}/onprop/{propuestaClienteId}/addcotizacionproveedor")
	public String processAddProposalProveedorToProposalCliente(@Valid WrapPropuestaProveedorAndSelectableAttributes wrapPropuestaProveedorAndSelectableAttributes, BindingResult bindingResult, Model model, @PathVariable(name = "consultaId") String consultaId, @PathVariable(name = "propuestaClienteId") String propuestaClienteId) {
		log.debug("el wrap que recibimos: " + wrapPropuestaProveedorAndSelectableAttributes.toString());
		if (bindingResult.hasErrors()) {
			model.addAttribute("wrapPropuestaProveedorAndSelectableAttributes", wrapPropuestaProveedorAndSelectableAttributes);
			model.addAttribute("consultaId", consultaId);
			return "addPropuestaProveedorToConsulta";
		}
		Propuesta propuesta = propuestaUtilities.getPropuestaProveedorFromWrapWithSelectableAttributes(wrapPropuestaProveedorAndSelectableAttributes);
		propuesta.setCreatedTime(System.currentTimeMillis());
		((PropuestaProveedor)propuesta).operationsProveedor().initializeStandardCosts();
		log.debug("propuesta convertida: " + propuesta.getId());
		Mono<Propuesta> p = consultaService.addPropuesta(consultaId, propuesta).flatMap(entity -> Mono.just(entity.operations().getPropuestaByIndex(entity.getPropuestas().size()-1)));
		model.addAttribute("propuesta", p);
		model.addAttribute("consultaId", consultaId);
		return "processAddPropuestaToConsulta";
	}
	
	@GetMapping("/costof/propid/{id}")
	public String showCostsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		Flux<CosteProveedor> afc = consultaService.findCostesByPropuestaId(proposalId);
		model.addAttribute("costes", new ReactiveDataDriverContextVariable(afc, 1));
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(proposalId);
		model.addAttribute("consulta", consulta);
		return "showCostsOfProposal";
	}
	
	@GetMapping("/costof/propid/{id}/edit")
	public Mono<String> editCostsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.map(cons -> {
				Propuesta prop = cons.operations().getPropuestaById(proposalId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("costesWrapper", new CostesWrapper(((PropuestaProveedor)prop).getCostes()));
				return "editCostOfProposal";
			})
			;
	}
	
	@PostMapping("/costof/propid/{id}/edit")
	public Mono<String> processEditCostsOfProposal(CostesWrapper costesWrapper, BindingResult bindingResult, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		PropuestaProveedorOperations.validateCosts(costesWrapper, bindingResult);
		if (bindingResult.hasErrors()) {
			return consultaService.findConsultaByPropuestaId(proposalId)
					.map(cons -> {
						Propuesta prop = cons.operations().getPropuestaById(proposalId);
						model.addAttribute("consulta", cons);
						model.addAttribute("propuesta", prop);
						model.addAttribute("costesWrapper", costesWrapper);
						return "editCostOfProposal";
					})
					;
		} else {
			return consultaService.updateCostesOfPropuesta(proposalId, costesWrapper.getCostes())
					.map(cons -> {
						Propuesta prop = cons.operations().getPropuestaById(proposalId);
						model.addAttribute("consulta", cons);
						model.addAttribute("propuesta", prop);
						model.addAttribute("costesWrapper", costesWrapper);
						return "processEditCostOfProposal";
					});
		}
	}
	
	@GetMapping("/costof/propid/{id}/new")
	public Mono<String> newCostOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
				.map(cons -> {
					Propuesta prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					model.addAttribute("costeProveedor", new CosteProveedor());
					return "newCostOfProposal";
				});
	}
	
	@PostMapping("/costof/propid/{id}/new")
	public Mono<String> processNewCostOfProposal(@Valid CosteProveedor costeProveedor, BindingResult bindingResult, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		if (bindingResult.hasErrors()) {
			return consultaService.findConsultaByPropuestaId(proposalId)
					.map(cons -> {
						Propuesta prop = cons.operations().getPropuestaById(proposalId);
						model.addAttribute("consulta", cons);
						model.addAttribute("propuesta", prop);
						model.addAttribute("costeProveedor", costeProveedor);
						return "newCostOfProposal";
					});
		}
		return consultaService.addCostToList(proposalId, costeProveedor)
				.map(cons -> {
					Propuesta prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					model.addAttribute("costeProveedor", costeProveedor);
					return "processNewCostOfProposal";
				})
				;
	}
	
	@GetMapping("/costof/propid/{id}/delete")
	public Mono<String> deleteCostsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.map(cons -> {
				Propuesta prop = cons.operations().getPropuestaById(proposalId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("costesCheckboxWrapper", new CostesCheckboxWrapper(((PropuestaProveedor)prop).operationsProveedor().getCostesCheckbox(modelMapper)));
				return "deleteCostsOfProposal";
			})
			;
	}
	
	@PostMapping("/costof/propid/{id}/delete")
	public Mono<String> confirmDeleteCostsOfProposal(CostesCheckboxWrapper costesCheckboxWraper, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.map(cons -> {
				Propuesta prop = cons.operations().getPropuestaById(proposalId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("costesCheckboxWrapper", costesCheckboxWraper);
				return "confirmDeleteCostsOfProposal";
			})
			;
	}
	
	@PostMapping("/costof/propid/{id}/delete/confirm")
	public Mono<String> processDeleteCostsOfProposal(CostesCheckboxWrapper costesCheckboxWraper, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.keepUnselectedCosts(proposalId, costesCheckboxWraper)
				.map(cons -> {
					Propuesta prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					model.addAttribute("costesCheckboxWrapper", new CostesCheckboxWrapper(((PropuestaProveedor)prop).operationsProveedor().getCostesCheckbox(modelMapper)));
					return "processDeleteCostsOfProposal";
				})
				;
	}
	
	@GetMapping("/costof/propid/{id}/order")
	public Mono<String> orderCostsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.map(cons -> {
				Propuesta prop = cons.operations().getPropuestaById(proposalId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("costesOrdenablesWrapper", new CostesOrdenablesWrapper(((PropuestaProveedor)prop).operationsProveedor().getCostesOrdenables(modelMapper)));
				return "orderCostsOfProposal";
			})
			;
	}
	
	@PostMapping("/costof/propid/{id}/order")
	public Mono<String> processOrderCostsOfProposal(CostesOrdenablesWrapper costesOrdenablesWraper, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.updateCostesOfPropuesta(proposalId, PropuestaProveedorOperations.fromCostesOrdenablesToCostesProveedor(modelMapper, costesOrdenablesWraper.getCostes()))
				.map(cons -> {
					Propuesta prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					model.addAttribute("costes", ((PropuestaProveedor)prop).getCostes());
					return "processOrderCostsOfProposal";
				})
				;
	}
	
	/**
	 * *************************
	 * OUR PROPOSALS
	 * *************************
	 */
	
	@GetMapping("/revisar/id/{consultaId}/onprop/{propuestaClienteId}/addofertanuestra")
	public String addOurOfferToProposalCliente(Model model, @PathVariable(name = "consultaId") String consultaId, @PathVariable(name = "propuestaClienteId") String propuestaClienteId) {
		model.addAttribute("consultaId", consultaId);
		Mono<WrapPropuestaNuestraAndSelectableAttributes> propuesta = propuestaUtilities.wrapPropuestaNuestraAndSelectableAttributes(new PropuestaNuestra(propuestaClienteId));
		model.addAttribute("wrapPropuestaNuestraAndSelectableAttributes", propuesta);
		return "addOurOfferToConsulta";
	}
	
	@PostMapping("/revisar/id/{consultaId}/onprop/{propuestaClienteId}/addofertanuestra")
	public String processAddOurOfferToProposalCliente(@Valid WrapPropuestaNuestraAndSelectableAttributes wrapPropuestaNuestraAndSelectableAttributes, BindingResult bindingResult, Model model, @PathVariable(name = "consultaId") String consultaId, @PathVariable(name = "propuestaClienteId") String propuestaClienteId) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("wrapPropuestaNuestraAndSelectableAttributes", wrapPropuestaNuestraAndSelectableAttributes);
			model.addAttribute("consultaId", consultaId);
			return "addOurOfferToConsulta";
		}
		Propuesta propuesta = propuestaUtilities.getPropuestaNuestraFromWrapWithSelectableAttributes(wrapPropuestaNuestraAndSelectableAttributes);
		propuesta.setCreatedTime(System.currentTimeMillis());
		Mono<Propuesta> p = consultaService.addPropuesta(consultaId, propuesta).flatMap(entity -> Mono.just(entity.operations().getPropuestaByIndex(entity.getPropuestas().size()-1)));
		model.addAttribute("propuesta", p);
		model.addAttribute("consultaId", consultaId);
		return "processAddPropuestaToConsulta";
	}
	
	@GetMapping("/pvpsof/propid/{id}")
	public Mono<String> showPvpsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
				.map(cons -> {
					var prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					if (prop instanceof PropuestaNuestra) {
						List<Pvper> pvps = ((PropuestaNuestra)prop).getPvps();
						List<PvperSum> sums = ((PropuestaNuestra)prop).getSums();
						model.addAttribute("pvps", pvps);
						model.addAttribute("sums", sums);
						model.addAttribute("map", cons.operations().mapIdToCosteProveedor());
						return "showPvpsOfProposal";
					} else {
						return "redirect:/consultas/revisar/id/" + cons.getId();
					}
				})
				;
	}
	
	@GetMapping("/pvpsof/propid/{id}/new")
	public Mono<String> newPvpOfPropuesta(Model model, @PathVariable(name = "id") String proposalId) {
		return addConsultaPropuestaAndIdFromPropuestaIdAndGetConsulta(model, proposalId)
				.map(cons -> {
					model.addAttribute("proveedores", cons.operations().getPropuestasProveedor());
					
					var consultaOperations = cons.operations();
					Propuesta prop = consultaOperations.getPropuestaById(proposalId);
					var operationsNuestra = ((PropuestaNuestra)prop).operationsNuestra();
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					model.addAttribute("proveedores", consultaOperations.getPropuestasProveedorAssignedTo(prop.getForProposalId()));
					model.addAttribute("costes", consultaOperations.getCostesOfPropuestasProveedorAssignedTo(prop.getForProposalId()));
					model.addAttribute("map", consultaOperations.mapIdToCosteProveedor());
					model.addAttribute("costToCotiz", costToCotiz.from(cons));
					model.addAttribute("atributos", consultaOperations.getAtributosOfPropuestasProveedorAssignedTo(prop.getForProposalId()));
					model.addAttribute("pvperCheckboxedCosts", operationsNuestra.getSinglePvpCheckboxed(modelMapper, consultaService, new Pvper()));
					return "newPvpOfProposal";
				});
	}
	
	@PostMapping("/pvpsof/propid/{id}/new")
	public Mono<String> processNewPvpOfPropuesta(@Valid PvperCheckboxedCosts pvperCheckboxedCosts, BindingResult bindingResult, Model model, @PathVariable(name = "id") String proposalId) {
		var idCostes = pvperCheckboxedCosts.getCosts();
		if (idCostes == null || idCostes.size() == 0) {bindingResult.rejectValue("costs", "error.costs", "Selecciona al menos un coste");}
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.flatMap(cons -> {
				var consultaOperations = cons.operations();
				Propuesta prop = consultaOperations.getPropuestaById(proposalId);
				var operationsNuestra = ((PropuestaNuestra)prop).operationsNuestra();
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("proveedores", consultaOperations.getPropuestasProveedor());
				model.addAttribute("costes", consultaOperations.getCostesOfPropuestasProveedor());
				model.addAttribute("costToCotiz", costToCotiz.from(cons));
				model.addAttribute("map", consultaOperations.mapIdToCosteProveedor());
				model.addAttribute("atributos", consultaOperations.getAtributosOfPropuestasProveedorAssignedTo(prop.getForProposalId()));
				model.addAttribute("pvperCheckboxedCosts", pvperCheckboxedCosts);
				if (bindingResult.hasErrors()) {
					return Mono.just("newPvpOfProposal");
				}
					Pvper pvper = pvperCheckboxedCostToPvper.from(pvperCheckboxedCosts);
					model.addAttribute("pvper", pvper);
					model.addAttribute("mapa", cons.operations().mapIdToCosteProveedor());
					return consultaService.addPvpToList(proposalId, pvper)
						.map(c -> "processNewPvpOfProposal");
				});
	}
	
	@GetMapping("/pvpsof/propid/{id}/delete")
	public Mono<String> deletePvpOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.map(cons -> {
				Propuesta prop = cons.operations().getPropuestaById(proposalId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("pvpsCheckboxWrapper", new PvpsCheckboxWrapper(((PropuestaNuestra)prop).operationsNuestra().getPvpsCheckbox(modelMapper)));
				return "deletePvpsOfProposal";
			})
			;
	}
	
	@PostMapping("/pvpsof/propid/{id}/delete")
	public Mono<String> confirmDeletePvpsOfProposal(PvpsCheckboxWrapper pvpsCheckboxWrapper, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.map(cons -> {
				Propuesta prop = cons.operations().getPropuestaById(proposalId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("pvpsCheckboxWrapper", pvpsCheckboxWrapper);
				return "confirmDeletePvpsOfProposal";
			})
			;
	}
	
	@PostMapping("/pvpsof/propid/{id}/delete/confirm")
	public Mono<String> processDeletePvpsOfProposal(PvpsCheckboxWrapper pvpsCheckboxWrapper, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.keepUnselectedPvps(proposalId, pvpsCheckboxWrapper)
				.map(cons -> {
					Propuesta prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					model.addAttribute("pvpsCheckboxWrapper", new PvpsCheckboxWrapper(((PropuestaNuestra)prop).operationsNuestra().getPvpsCheckbox(modelMapper)));
					return "processDeletePvpsOfProposal";
				})
				;
	}
	
	@GetMapping("/pvpsof/propid/{id}/order")
	public Mono<String> orderPvpsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.map(cons -> {
				Propuesta prop = cons.operations().getPropuestaById(proposalId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("pvpsOrdenablesWrapper", new PvpsOrdenablesWrapper(((PropuestaNuestra)prop).operationsNuestra().getPvpsOrdenables(modelMapper)));
				model.addAttribute("map", cons.operations().mapIdToCosteProveedor());
				return "orderPvpsOfProposal";
			})
			;
	}
	
	@PostMapping("/pvpsof/propid/{id}/order")
	public Mono<String> processOrderPvpsOfProposal(PvpsOrdenablesWrapper pvpsOrdenablesWrapper, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.updatePvpsOfPropuesta(proposalId, PropuestaNuestraOperations.fromPvpsOrdenablesToPvper(modelMapper, pvpsOrdenablesWrapper.getPvps()))
				.map(cons -> {
					Propuesta prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					model.addAttribute("pvps", ((PropuestaNuestra)prop).getPvps());
					model.addAttribute("map", cons.operations().mapIdToCosteProveedor());
					return "processOrderPvpsOfProposal";
				})
				;
	}
	
	// BULK EDIT
	@GetMapping("/pvpsof/propid/{id}/edit")
	public Mono<String> editPvpsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.map(cons -> {
				Propuesta prop = cons.operations().getPropuestaById(proposalId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("proveedores", cons.operations().getPropuestasProveedor());
				model.addAttribute("costes", cons.operations().getCostesOfPropuestasProveedor());
				model.addAttribute("map", cons.operations().mapIdToCosteProveedor());
				model.addAttribute("pvpsCheckboxedCostWrapper",((PropuestaNuestra)prop).operationsNuestra().getPvpsCheckbox(modelMapper, consultaService));
				return "editPvpsOfProposal";
			})
			;
	}
	
	// PROCESS BULK EDIT
	@PostMapping("/pvpsof/propid/{id}/edit")
	public Mono<String> processEditPvpsOfProposal(PvpsCheckboxedCostWrapper pvpsCheckboxedCostWrapper, BindingResult bindingResult, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		PropuestaNuestraOperations.validateNamesAndCostsOfCheckboxedWrapper(pvpsCheckboxedCostWrapper, bindingResult);
		if (bindingResult.hasErrors()) {
			return consultaService.findConsultaByPropuestaId(proposalId)
					.map(cons -> {
						Propuesta prop = cons.operations().getPropuestaById(proposalId);
						model.addAttribute("consulta", cons);
						model.addAttribute("propuesta", prop);
						model.addAttribute("proveedores", cons.operations().getPropuestasProveedor());
						model.addAttribute("map", cons.operations().mapIdToCosteProveedor());
						model.addAttribute("pvpsCheckboxedCostWrapper", pvpsCheckboxedCostWrapper);
						return "editPvpsOfProposal";
					});
		} else {
			return consultaService.updatePvpsOfPropuesta(proposalId, PropuestaNuestraOperations.fromCheckboxedWrapperToPvps(pvpsCheckboxedCostWrapper))
					.map(cons -> {
						Propuesta prop = cons.operations().getPropuestaById(proposalId);
						model.addAttribute("consulta", cons);
						model.addAttribute("propuesta", prop);
						model.addAttribute("map", cons.operations().mapIdToCosteProveedor());
						model.addAttribute("pvpsCheckboxedCostWrapper", pvpsCheckboxedCostWrapper);
						return "processEditPvpsOfProposal";
					})
					;
		}
	}
	
	// INDIVIDUAL EDIT
	@GetMapping("/pvpsof/propid/{propid}/edit/{pvpid}")
	public Mono<String> editPvpOfProposal(Model model, @PathVariable(name = "propid") String proposalId, @PathVariable(name = "pvpid") String pvpId) {
		model.addAttribute("propuestaId", proposalId);
		model.addAttribute("pvpId", pvpId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.map(cons -> {
				var consultaOperations = cons.operations();
				Propuesta prop = consultaOperations.getPropuestaById(proposalId);
				var operationsNuestra = ((PropuestaNuestra)prop).operationsNuestra();
				Pvper pvp = operationsNuestra.getPvpById(pvpId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("proveedores", consultaOperations.getPropuestasProveedor());
				model.addAttribute("costes", consultaOperations.getCostesOfPropuestasProveedor());
				model.addAttribute("costToCotiz", costToCotiz.from(cons));
				model.addAttribute("map", consultaOperations.mapIdToCosteProveedor());
				model.addAttribute("atributos", consultaOperations.getAtributosOfPropuestasProveedorAssignedTo(prop.getForProposalId()));
				model.addAttribute("pvperCheckboxedCosts", operationsNuestra.getSinglePvpCheckboxed(modelMapper, consultaService, pvp));
				return "editPvpOfProposal";
			})
			;
	}
	
	// PROCESS INDIVIDUAL EDIT
	@PostMapping("/pvpsof/propid/{propid}/edit/{pvpid}")
	public Mono<String> processEditPvpOfProposal(@Valid PvperCheckboxedCosts pvperCheckboxedCosts, BindingResult bindingResult, Model model, @PathVariable(name = "propid") String proposalId, @PathVariable(name = "pvpid") String pvpId) {
		var idCostes = pvperCheckboxedCosts.getCosts();
		if (idCostes == null || idCostes.size() == 0) {bindingResult.rejectValue("costs", "error.costs", "Selecciona al menos un coste");}
		model.addAttribute("propuestaId", proposalId);
		model.addAttribute("pvpId", pvpId);
		return consultaService.findConsultaByPropuestaId(proposalId)
			.flatMap(cons -> {
				var consultaOperations = cons.operations();
				Propuesta prop = consultaOperations.getPropuestaById(proposalId);
				var operationsNuestra = ((PropuestaNuestra)prop).operationsNuestra();
				Pvper pvp = operationsNuestra.getPvpById(pvpId);
				model.addAttribute("consulta", cons);
				model.addAttribute("propuesta", prop);
				model.addAttribute("proveedores", consultaOperations.getPropuestasProveedor());
				model.addAttribute("costes", consultaOperations.getCostesOfPropuestasProveedor());
				model.addAttribute("costToCotiz", costToCotiz.from(cons));
				model.addAttribute("map", consultaOperations.mapIdToCosteProveedor());
				model.addAttribute("atributos", consultaOperations.getAtributosOfPropuestasProveedorAssignedTo(prop.getForProposalId()));
				model.addAttribute("pvperCheckboxedCosts", pvperCheckboxedCosts);
				if (bindingResult.hasErrors()) {
					return Mono.just("editPvpOfProposal");
				}
				Pvper pvper = pvperCheckboxedCostToPvper.from(pvperCheckboxedCosts);
				return consultaService.updateSinglePvpOfPropuesta(proposalId, pvper)
						.then(Mono.just("processEditPvpOfProposal"));
			})
			;
	}
	
	/**
	 * *******************************
	 * PVP SUMS
	 * *******************************
	 */
	
	@GetMapping("/pvpsumsof/propid/{id}")
	public Mono<String> showPvpSumsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
				.map(cons -> {
					var prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					if (prop instanceof PropuestaNuestra) {
						List<Pvper> pvps = ((PropuestaNuestra)prop).getPvps();
						List<PvperSum> sums = ((PropuestaNuestra)prop).getSums();
						model.addAttribute("propuesta", prop);
						model.addAttribute("pvps", pvps);
						model.addAttribute("sums", sums);
						model.addAttribute("map", ((PropuestaNuestra)prop).operationsNuestra().mapIdToPvper());
						return "showPvpsumsOfProposal";
					} else {
						return "redirect:/consultas/revisar/id/" + cons.getId();
					}
				})
				;
	}
	
	@GetMapping("/pvpsumsof/propid/{id}/new")
	public Mono<String> newPvpSumOfPropuesta(Model model, @PathVariable(name = "id") String proposalId) {
		return addConsultaPropuestaAndIdFromPropuestaIdAndGetConsulta(model, proposalId)
				.map(cons -> {
					model.addAttribute("pvps", ((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).getPvps());
					model.addAttribute("pvperSum", new PvperSum());
					return "newPvpSumOfProposal";
				})
				;
	}
	
	@PostMapping("/pvpsumsof/propid/{id}/new")
	public Mono<String> processNewPvpSumOfPropuesta(@Valid @ModelAttribute("pvperSum") PvperSum pvperSum, BindingResult bindingResult, Model model, @PathVariable(name = "id") String proposalId) {
		var idPvps = pvperSum.getPvperIds();
		if (idPvps == null || idPvps.size() == 0) {bindingResult.rejectValue("pvperIds", "error.pvperIds", "Selecciona al menos 1 PVP");}
		return addConsultaPropuestaAndIdFromPropuestaIdAndGetConsulta(model, proposalId)
			.flatMap(cons -> {
				model.addAttribute("pvperSum", pvperSum);
				if (bindingResult.hasErrors()) {
					model.addAttribute("pvps", ((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).getPvps());
					return Mono.just("newPvpSumOfProposal");
				} else {
					model.addAttribute("mapa", ((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).operationsNuestra().mapIdToPvper());
					return consultaService.addPvpSumToList(proposalId, pvperSum)
							.map(c -> "processNewPvpSumOfPropuesta");
				}
			})
			;
	}
	
	@GetMapping("/pvpsumsof/propid/{id}/delete")
	public Mono<String> deletePvpSumOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		return addConsultaPropuestaAndIdFromPropuestaIdAndGetConsulta(model, proposalId)
				.map(cons -> {
					model.addAttribute("pvperSumCheckboxWrapper", new PvperSumCheckboxWrapper(((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).operationsNuestra().getPvpSumsCheckbox(modelMapper)));
					return "deletePvpSumsOfProposal";
				})
				;
	}
	
	@PostMapping("/pvpsumsof/propid/{id}/delete")
	public Mono<String> confirmDeletePvpSumOfProposal(PvperSumCheckboxWrapper pvperSumCheckboxWrapper, Model model, @PathVariable(name = "id") String proposalId) {
		return addConsultaPropuestaAndIdFromPropuestaIdAndGetConsulta(model, proposalId)
				.map(cons -> {
					model.addAttribute("pvperSumCheckboxWrapper", pvperSumCheckboxWrapper);
					return "confirmDeletePvpSumOfProposal";
				})
				;
	}
	
	@PostMapping("/pvpsumsof/propid/{id}/delete/confirm")
	public Mono<String> processDeletePvpSumOfProposal(PvperSumCheckboxWrapper pvperSumCheckboxWrapper, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.keepUnselectedPvpSums(proposalId, pvperSumCheckboxWrapper)
				.map(cons -> {
					Propuesta prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					model.addAttribute("pvperSumCheckboxWrapper", new PvperSumCheckboxWrapper(((PropuestaNuestra)prop).operationsNuestra().getPvpSumsCheckbox(modelMapper)));
					return "processDeletePvpSumsOfProposal";
				})
				;
	}
	
	@GetMapping("/pvpsumsof/propid/{id}/order")
	public Mono<String> orderPvpSumsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		return addConsultaPropuestaAndIdFromPropuestaIdAndGetConsulta(model, proposalId)
				.map(cons -> {
					model.addAttribute("pvperSumsOrdenablesWrapper", new PvperSumsOrdenablesWrapper(((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).operationsNuestra().getPvpSumsOrdenables(modelMapper)));
					model.addAttribute("map", ((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).operationsNuestra().mapIdToPvper());
					return "orderPvpSumsOfProposal";
				})
				;
	}
	
	@PostMapping("/pvpsumsof/propid/{id}/order")
	public Mono<String> processOrderPvpSumsOfProposal(PvperSumsOrdenablesWrapper pvperSumsOrdenablesWrapper, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.updatePvpSumsOfPropuesta(proposalId, PropuestaNuestraOperations.fromPvperSumOrdernablesToPvperSums(modelMapper, pvperSumsOrdenablesWrapper.getSums()))
				.map(cons -> {
					Propuesta prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					model.addAttribute("sums", ((PropuestaNuestra)prop).getSums());
					model.addAttribute("map", ((PropuestaNuestra)prop).operationsNuestra().mapIdToPvper());
					return "processOrderPvpSumsOfProposal";
				})
				;
	}
	
	@GetMapping("/pvpsumsof/propid/{id}/edit")
	public Mono<String> editPvpSumsOfProposal(Model model, @PathVariable(name = "id") String proposalId) {
		return addConsultaPropuestaAndIdFromPropuestaIdAndGetConsulta(model, proposalId)
				.map(cons -> {
					model.addAttribute("pvps", ((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).getPvps());
					model.addAttribute("map", ((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).operationsNuestra().mapIdToPvper());
					model.addAttribute("pvperSumCheckboxedPvpsWrapper", ((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).operationsNuestra().getPvpSumCheckboxedPvpsWrapper(modelMapper, consultaService));
					return "editPvpSumsOfProposal";
				})
				;
	}
	
	@PostMapping("/pvpsumsof/propid/{id}/edit")
	public Mono<String> processEditPvpSumsOfProposal(PvperSumCheckboxedPvpsWrapper pvperSumCheckboxedPvpsWrapper, BindingResult bindingResult, Model model, @PathVariable(name = "id") String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		PropuestaNuestraOperations.validateNamesAndCostsOfSumsCheckboxedWrapper(pvperSumCheckboxedPvpsWrapper, bindingResult);
		if (bindingResult.hasErrors()) {
			return addConsultaPropuestaAndIdFromPropuestaIdAndGetConsulta(model, proposalId)
					.map(cons -> {
						model.addAttribute("pvps", ((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).getPvps());
						model.addAttribute("map", ((PropuestaNuestra)cons.operations().getPropuestaById(proposalId)).operationsNuestra().mapIdToPvper());
						model.addAttribute("pvperSumCheckboxedPvpsWrapper", pvperSumCheckboxedPvpsWrapper);
						return "editPvpSumsOfProposal";
					})
					;
		} else {
			return consultaService.updatePvpSumsOfPropuesta(proposalId, PropuestaNuestraOperations.fromSumCheckboxedWrapperToPvps(pvperSumCheckboxedPvpsWrapper))
					.map(cons -> {
						var prop = cons.operations().getPropuestaById(proposalId);
						model.addAttribute("consulta", cons);
						model.addAttribute("propuesta", prop);
						model.addAttribute("map", ((PropuestaNuestra)prop).operationsNuestra().mapIdToPvper());
						model.addAttribute("pvperSumCheckboxedPvpsWrapper", pvperSumCheckboxedPvpsWrapper);
						return "processEditPvpSumsOfProposal";
					})
					;
		}
	}
	
	/**
	 * REPETITIVE ACTIONS
	 */
	
	private Mono<Consulta> addConsultaPropuestaAndIdFromPropuestaIdAndGetConsulta(Model model, String proposalId) {
		model.addAttribute("propuestaId", proposalId);
		return consultaService.findConsultaByPropuestaId(proposalId)
				.map(cons -> {
					Propuesta prop = cons.operations().getPropuestaById(proposalId);
					model.addAttribute("consulta", cons);
					model.addAttribute("propuesta", prop);
					return cons;
				});
	}
	
}
