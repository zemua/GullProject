package devs.mrp.gullproject.controller;

import java.util.ArrayList;

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
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AttributesListDto;
import devs.mrp.gullproject.domains.dto.ConsultaPropuestaBorrables;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping(path = "/consultas")
public class ConsultaController {
	
	// TODO re-order attribute columns into proposal

	ConsultaService consultaService;
	LineaService lineaService;
	AtributoServiceProxyWebClient atributoService;
	private final ModelMapper modelMapper;
	
	@Autowired
	public ConsultaController(ConsultaService consultaService, LineaService lineaService, AtributoServiceProxyWebClient atributoService, ModelMapper modelMapper) {
		this.consultaService = consultaService;
		this.lineaService = lineaService;
		this.atributoService = atributoService;
		this.modelMapper = modelMapper;
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
	
	@GetMapping("/revisar/id/{id}") // TODO make possible to edit the status of the inquiry
	public String reviewConsultaById(Model model, @PathVariable(name = "id") String id) {
		Mono<Consulta> consulta = consultaService.findById(id);
		model.addAttribute("consulta", consulta);
		return "reviewConsulta";
	}
	
	@GetMapping("/revisar/id/{id}/addpropuesta")
	public String addPropuestaToId(Model model, @PathVariable(name= "id") String id) {
		model.addAttribute("consultaId", id);
		model.addAttribute("propuestaCliente", new PropuestaCliente() {});
		/** 
		 * here by default we add an inquiry from the customer first of all, when there are no any inquiry in the database
		 * our proposals and the offers received from the suppliers, will be added from the customer's inquiry view
		 * with this we can automatically associate the customer inquiry with the propossals that refer to it and have everything well organized
		 */
		return "addPropuestaToConsulta";
	}
	
	@PostMapping("/revisar/id/{id}")
	public String processAddPropuestaToId(@Valid PropuestaCliente propuestaCliente, BindingResult bindingResult, Model model, @PathVariable(name ="id") String id) {
		log.debug(propuestaCliente.toString());
		if (bindingResult.hasErrors()) {
			log.debug("processAddPropuestaToId -> invalid PropuestaCliente received by POST");
			log.debug(bindingResult.toString());
			model.addAttribute("propuestaCliente", propuestaCliente);
			model.addAttribute("consultaId", id);
			return "addPropuestaToConsulta";
		}
		
		propuestaCliente.setParentId("-1");
		propuestaCliente.setId(new ObjectId().toString());
		log.debug("propuesta id: " + propuestaCliente.getId());
		Mono<Propuesta> c = consultaService.addPropuesta(id, propuestaCliente).flatMap(entity -> Mono.just(entity.getPropuestaByIndex(entity.getCantidadPropuestas()-1)));
		model.addAttribute("propuestaCliente", c);
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
			numPropuestas = cons.flatMap(cc -> Mono.just(cc.getCantidadPropuestas()));
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
		Mono<Propuesta> p = consultaService.findById(consultaid).flatMap(cons -> Mono.just(cons.getPropuestaById(propuestaid)));
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
		
		Mono<AttributesListDto> atributos = consultaService.findPropuestaByPropuestaId(proposalId)
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
		
		model.addAttribute("atts", atributos);
		return "addAttributeToProposal";
	}
	
	@PostMapping("/attof/propid/{id}/new")
	public String processAddAttributeToProposal(@ModelAttribute AttributesListDto atts, BindingResult bindingResult, Model model, @PathVariable(name = "id") String propuestaId) {
		
		Flux<AtributoForCampo> attributes;
		
		if (atts.getAttributes() == null || atts.getAttributes().size() == 0) {
			attributes = Flux.fromIterable(new ArrayList<AtributoForCampo>());
		} else {
			attributes = Flux.fromIterable(atts.getAttributes())
					.filter(a -> a.getSelected())
					.map(a -> modelMapper.map(a, AtributoForCampo.class));
		}
		
		Mono<Consulta> consulta = attributes.collectList()
				.flatMap(latts -> consultaService.updateAttributesOfPropuesta(propuestaId, latts));
		
		Mono<Propuesta> propuesta = consulta.map(c -> c.getPropuestaById(propuestaId));

		model.addAttribute("atributos", new ReactiveDataDriverContextVariable(attributes, 1));
		model.addAttribute("propuesta", propuesta);
		
		return "processAddAttributeToProposal";
	}
	
}
