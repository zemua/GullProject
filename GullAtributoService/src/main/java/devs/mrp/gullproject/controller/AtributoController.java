package devs.mrp.gullproject.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Flux;

@Controller
@RequestMapping(path = "/atributos")
public class AtributoController {
	
	// TODO test

	AtributoService atributoService;
	
	@Autowired
	public AtributoController(AtributoService atributoService) {
		this.atributoService=atributoService;
	}
	
	@GetMapping("/nuevo")
	public String crearAtributo(Model model) {
		
		model.addAttribute("atributo", new Atributo());
		model.addAttribute("tipos", DataFormat.values());
		
		return "crearAtributo";
	}
	
	@PostMapping("/nuevo")
	public String procesaNuevoAtributo(@Valid Atributo atributo, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "/nuevo";
		}
		
		atributoService.save(atributo).subscribe();
		
		return "/nuevo?add=1";
	}
	
	@GetMapping("/todos")
	public String mostrarAtributos(Model model) {
		
		Flux<Atributo> atributos = atributoService.findAll();
		model.addAttribute("atributos", new ReactiveDataDriverContextVariable(atributos, 1));
		
		return "mostrarAtributos";
	}
	
}
