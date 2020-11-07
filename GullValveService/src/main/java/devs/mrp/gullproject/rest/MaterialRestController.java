package devs.mrp.gullproject.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.domains.representationmodels.MaterialRepresentationModel;
import devs.mrp.gullproject.domains.representationmodels.representationmodelassemblersupport.MaterialRepresentationModelAssembler;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import reactor.core.publisher.Flux;

import org.springframework.http.MediaType;

@RestController
@RequestMapping(value = "/api/materiales",  produces = MediaType.APPLICATION_JSON_VALUE)
public class MaterialRestController {

	// TODO hacer service entre repository y restController
	// TODO hacer test
	
	private final MediaType mediaType = MediaType.APPLICATION_JSON;
	private final MaterialRepo materialRepo;
	private final MaterialRepresentationModelAssembler mrma;
	
	public MaterialRestController(MaterialRepo materialRepo, MaterialRepresentationModelAssembler mrma) {
		this.materialRepo = materialRepo;
		this.mrma = mrma;
	}
	
	@GetMapping
	public Flux<MaterialRepresentationModel> getAllMaterials() {
		Flux<Material> ms = materialRepo.findAll();
		Flux<MaterialRepresentationModel> mrm = ms.map(e -> mrma.toModel(e));
		
		return mrm;
	}
	
}
