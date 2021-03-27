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

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping(path = "/atributos")
public class AtributoController {
	
	// falta añadir handler para situaciones de error como cuando el "id" de la url no existe en db
	
	// TODO create value maps for atributes that will have defined values, like the DN or the rating of the valves

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
	public String procesaNuevoAtributo(@Valid Atributo atributo, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("atributo", atributo);
			model.addAttribute("tipos", DataFormat.values());
			return "crearAtributo";
		}
		
		atributoService.save(atributo).subscribe();
		
		return "redirect:/atributos/nuevo?add=1";
	}
	
	@GetMapping("/todos")
	public String mostrarAtributos(Model model) {
		
		Flux<Atributo> atributos = atributoService.findAll();
		model.addAttribute("atributos", new ReactiveDataDriverContextVariable(atributos, 1));
		//model.addAttribute("atributos", atributos);
		
		return "mostrarAtributos";
	}
	
	// TODO no permitir modificar el class type de los atributos, por datos existentes
	@GetMapping("/editar/id/{id}")
	public String editarAtributo(Model model, @PathVariable(name = "id") String id) {
		
		model.addAttribute("atributo", atributoService.findById(id));
		model.addAttribute("tipos", DataFormat.values());
		
		return "editarAtributo";
	}
	
	@PostMapping("/actualizar/id/{id}")
	public String actualizarAtributo(@Valid Atributo atributo, BindingResult bindingResult, Model model, @PathVariable(name = "id") String id) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("atributo", atributo);
			model.addAttribute("tipos", DataFormat.values());
			//return "redirect:/atributos/editar/id/" + id;
			return "editarAtributo";
		}
		
		Mono<Atributo> a = atributoService.save(atributo);
		model.addAttribute("atributo", a);
		
		return "actualizarAtributo";
		
	}
	
	@GetMapping("/borrar/id/{id}")
	public String borrarAtributo(Model model, @PathVariable(name = "id") String id) {
		
		Mono<Atributo> a = atributoService.findById(id);
		model.addAttribute("atributo", a);
		
		return "borrarAtributo";
	}
	
	@PostMapping("/borrar/id/{id}")
	public String confirmBorrarAtributo(@Valid Atributo atributo, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "error";
		}
		
		
		atributoService.deleteById(atributo.getId()).subscribe();
		
		return "borradoAtributo";
	}
	
}
