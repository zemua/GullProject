package devs.mrp.gullproject.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/material", produces = MediaType.APPLICATION_JSON_VALUE)
public class MaterialByIdRestController {

	// TODO test
	
	private final MediaType mediaType = MediaType.APPLICATION_JSON;
	private final MaterialRepo materialRepo;
	
	public MaterialByIdRestController(MaterialRepo materialRepo) {
		this.materialRepo = materialRepo;
	}
	
	@GetMapping("/{id}")
	public Mono<Material> getMaterialById(@PathVariable(value = "id") String id) {
		Mono<Material> mm = materialRepo.findById(id);
		return mm;
	}
}
