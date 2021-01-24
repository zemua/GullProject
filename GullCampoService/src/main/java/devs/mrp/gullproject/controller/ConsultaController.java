package devs.mrp.gullproject.controller;

import java.util.Arrays;

import javax.validation.Valid;

import org.bson.types.ObjectId;
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

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaAbstracta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.dto.ConsultaPropuestaBorrables;
import devs.mrp.gullproject.domains.models.ConsultaRepresentationModel;
import devs.mrp.gullproject.service.ConsultaService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping(path = "/consultas")
public class ConsultaController {
	
	// TODO completar todas las funcionalidades

	ConsultaService consultaService;
	
	@Autowired
	public ConsultaController(ConsultaService consultaService) {
		this.consultaService = consultaService;
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
		return "reviewConsulta";
	}
	
	@GetMapping("/revisar/id/{id}/addpropuesta")
	public String addPropuestaToId(Model model, @PathVariable(name= "id") String id) {
		model.addAttribute("consultaId", id);
		model.addAttribute("propuestaCliente", new PropuestaAbstracta() {});
		/** here by default we add an inquiry from the customer first of all, when there are no any inquiry in the database
		 * 	our proposals and the offers received from the suppliers, will be added from the customer's inquiry view
		 *  with this we can automatically associate the customer inquiry with the propossals that refer to it and have everything well organized
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
		if (consulta.getId().equals(id)) {
			log.debug("idConsulta equals id");
			c = consultaService.deleteById(consulta.getId());
			// TODO borrar también las líneas que referencian a las propuestas de esta consulta
		} else {
			log.debug("idConsulta does not equal id");
			c = Mono.empty();
		}
		model.addAttribute("count", c);
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
		Mono<Consulta> c = consultaService.removePropuestaById(data.getIdConsulta(), data.getIdPropuesta());
		c.subscribe();
		model.addAttribute("idConsulta", data.getIdConsulta());
		
		return "processDeletePropuestaById";
	}
	
}
