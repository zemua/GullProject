package devs.mrp.gullproject.Controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.service.ConsultaService;
import lombok.extern.slf4j.Slf4j;
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
			//log.info("processNewConsulta entrando en la rama de error");
			model.addAttribute("consulta", consulta);
			return "createConsulta";
		}
		
		//log.info("consulta dentro de processNewConsulta es = " + consulta);
		Mono<Consulta> c = consultaService.save(consulta);
		//log.info("mono dentro de processNewConsulta es = " + c.toString());
		model.addAttribute("consulta", c);
		
		return "processNewConsulta";
	}
	
}
