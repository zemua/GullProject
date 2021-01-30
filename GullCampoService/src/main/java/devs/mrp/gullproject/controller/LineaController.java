package devs.mrp.gullproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
		// TODO test
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId",propuestaId);
		return "addLineaToPropuesta";
	}
	
}
