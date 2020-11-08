package devs.mrp.gullproject.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Controller
@RequestMapping(path = "/material")
public class MaterialController {
	
	MaterialRepo materialRepo;
	
	@Autowired
	public MaterialController(MaterialRepo materialRepo) {
		this.materialRepo = materialRepo;
	}
	
	@GetMapping("/nuevo")
	public String crearMaterial(Model model) {
		
		model.addAttribute("material", new Material());
		
		return "crearMaterial";
	}
	
	@PostMapping("/nuevo")
	public String procesaMaterial(@Valid Material material, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "/nuevo";
		}
		
		materialRepo.save(material).subscribe();
				
		return "redirect:/material/todos";
	}
	
	@GetMapping("/todos")
	public String mostrarMateriales(Model model) {
		
		Flux<Material> ms = materialRepo.findAll();
		model.addAttribute("materiales", new ReactiveDataDriverContextVariable(ms, 1));
		//model.addAttribute("materiales", ms);
		
		return "mostrarMateriales";
	}
}
