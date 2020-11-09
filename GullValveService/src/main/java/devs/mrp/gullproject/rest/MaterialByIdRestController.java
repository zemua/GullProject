package devs.mrp.gullproject.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.domains.representationmodels.MaterialRepresentationModel;
import devs.mrp.gullproject.domains.representationmodels.representationmodelassemblersupport.MaterialRepresentationModelAssembler;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/material", produces = MediaType.APPLICATION_JSON_VALUE)
public class MaterialByIdRestController {
	
	private final MediaType mediaType = MediaType.APPLICATION_JSON;
	private final MaterialRepo materialRepo;
	private final MaterialRepresentationModelAssembler mrma;
	
	public MaterialByIdRestController(MaterialRepo materialRepo, MaterialRepresentationModelAssembler mrma) {
		this.materialRepo = materialRepo;
		this.mrma = mrma;
	}
	
	@GetMapping("/{id}")
	public Mono<MaterialRepresentationModel> getMaterialById(@PathVariable(value = "id") String id) {
		Mono<Material> mm = materialRepo.findById(id);
		Mono<MaterialRepresentationModel> mrm = mm.map(a -> mrma.toModel(a));
		return mrm;
	}
}
