package devs.mrp.gullproject.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping(path = "/lineas")
public class LineaController {

	private LineaService lineaService;
	private ConsultaService consultaService;
	
	@Autowired
	public LineaController(LineaService lineaService, ConsultaService consultaService) {
		this.lineaService = lineaService;
		this.consultaService = consultaService;
	}
	
	@GetMapping("/allof/propid/{propuestaId}")
	public String showAllLinesOf(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Flux<Linea> lineas = lineaService.findByPropuestaId(propuestaId);
		model.addAttribute("lineas", new ReactiveDataDriverContextVariable(lineas, 1));
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId",propuestaId);
		return "showAllLineasOfPropuesta";
	}
	
	@GetMapping("/of/{propuestaId}/new")
	public String addLineToPropuesta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		model.addAttribute("propuesta", propuesta);
		model.addAttribute("propuestaId",propuestaId);
		model.addAttribute("linea", new Linea());
		return "addLineaToPropuesta";
	}
	
	@PostMapping("/of/{propuestaId}/new") // TODO solve error
	public String processAddLineaToPropuesta(@Valid Linea linea, BindingResult bindingResult, Model model, @PathVariable(name ="propuestaId") String propuestaId) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("propuesta", consultaService.findPropuestaByPropuestaId(propuestaId));
			model.addAttribute("propuestaId", propuestaId);
			model.addAttribute("linea", linea);
			return "addLineaToPropuesta";
		}
		Mono<Linea> l1;
		Mono<Propuesta> p1;
		if (linea.getPropuestaId().equals(propuestaId)){
			log.debug("propuestaId's equal");
			l1 = lineaService.addLinea(linea);
			p1 = consultaService.findPropuestaByPropuestaId(propuestaId);
		} else {
			log.debug("propuestaId's are NOT equal");
			l1 = Mono.empty();
			p1 = Mono.empty();
		}
		model.addAttribute("linea", l1);
		model.addAttribute("propuesta", p1);
		return "processAddLineaToPropuesta";
	}
	
}
