package devs.mrp.gullproject.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.domains.Valvula;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import devs.mrp.gullproject.repositorios.ParteRepo;
import devs.mrp.gullproject.repositorios.PiezaRepo;
import devs.mrp.gullproject.repositorios.TipoRepo;
import devs.mrp.gullproject.repositorios.ValvulaRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping(path = "/valvula")
public class ValveController {
	
	ValvulaRepo valvulaRepo;
	TipoRepo tipoRepo;
	PiezaRepo piezaRepo;
	ParteRepo parteRepo;
	MaterialRepo materialRepo;
	
	
	@Autowired
	public ValveController(ValvulaRepo valvulaRepo, TipoRepo tipoRepo, PiezaRepo piezaRepo, ParteRepo parteRepo, MaterialRepo materialRepo) {
		this.valvulaRepo = valvulaRepo;
		this.tipoRepo = tipoRepo;
		this.piezaRepo = piezaRepo;
		this.parteRepo = parteRepo;
		this.materialRepo = materialRepo;
	}
	
	@GetMapping(value = {"/nueva", "/nueva/added"})
	public String formularioNueva(Model model) {
		model.addAttribute("valvula", new Valvula());
		
		IReactiveDataDriverContextVariable tipos = new ReactiveDataDriverContextVariable(tipoRepo.findAll(), 1);
		model.addAttribute("tipos", tipos);
		
		IReactiveDataDriverContextVariable piezas = new ReactiveDataDriverContextVariable(piezaRepo.findAll(), 1);
		model.addAttribute("piezas", piezas);
		
		return "nuevaValvula";
	}
	
	@PostMapping("/nueva")
	public String processNueva(@Valid @ModelAttribute("valvula") Valvula valvula, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "nueva";
		}
		
		
		return "redirect:/nueva/added";
	}

	@GetMapping("/reciente")
	public Flux<Valvula> valvulasRecientes() {
		return valvulaRepo.findAll().take(12);
	}
	
	@GetMapping("/{id}")
	public Mono<Valvula> valvulaPorId(@PathVariable("id") String id) {
		return valvulaRepo.findById(id);
	}
	
	@PostMapping(consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Valvula> postValvula(@RequestBody Mono<Valvula> valvulaMono){
		return valvulaRepo.saveAll(valvulaMono).next();
	}
	
}
