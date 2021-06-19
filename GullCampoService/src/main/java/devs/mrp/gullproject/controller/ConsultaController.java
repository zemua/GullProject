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
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AttributesListDto;
import devs.mrp.gullproject.domains.dto.ConsultaPropuestaBorrables;
import devs.mrp.gullproject.domains.dto.CostesWrapper;
import devs.mrp.gullproject.domains.dto.ProposalPie;
import devs.mrp.gullproject.domains.dto.WrapAtributosForCampoDto;
import devs.mrp.gullproject.domains.dto.WrapPropuestaClienteAndSelectableAttributes;
import devs.mrp.gullproject.domains.dto.WrapPropuestaProveedorAndSelectableAttributes;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import devs.mrp.gullproject.service.PropuestaProveedorOperations;
import devs.mrp.gullproject.service.PropuestaUtilities;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping(path = "/consultas")
public class ConsultaController {
	
	// TODO create supplier proposals from customer ones (discriminar lineas con el mismo nombre y distinto precio)
	// TODO create our proposals from customer and supplier ones
	// TODO adapt old supplier proposals for updated customer inquiry
	// TODO make a "compare" view to check supplier vs customer table and ours vs customer table
	
	ConsultaService consultaService;
	LineaService lineaService;
	AtributoServiceProxyWebClient atributoService;
	PropuestaUtilities propuestaUtilities;
	
	@Autowired
	public ConsultaController(ConsultaService consultaService, LineaService lineaService, AtributoServiceProxyWebClient atributoService, PropuestaUtilities propuestaUtilities) {
		this.consultaService = consultaService;
		this.lineaService = lineaService;
		this.atributoService = atributoService;
		this.propuestaUtilities = propuestaUtilities;
	}
	
	@GetMapping("/nuevo")
	public String createConsulta(Model model) {
		model.addAttribute("consulta", new Consulta());
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
	public String processDeletePropuestaById(ConsultaPropuestaBorrables data, Model model) {
		log.debug("id consulta: " + data.getIdConsulta());
		log.debug("id propuesta: " + data.getIdPropuesta());
		Mono<Integer> c = consultaService.removePropuestaById(data.getIdConsulta(), data.getIdPropuesta());
		Mono<Long> lineas = lineaService.deleteSeveralLineasFromPropuestaId(data.getIdPropuesta());
		model.addAttribute("idConsulta", data.getIdConsulta());
		model.addAttribute("propuestasBorradas", c);
		model.addAttribute("lineasBorradas", lineas);
		return "processDeletePropuestaById";
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
	
}
