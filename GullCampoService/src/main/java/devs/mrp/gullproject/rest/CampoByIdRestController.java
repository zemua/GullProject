package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.models.CampoRepresentationModel;
import devs.mrp.gullproject.domains.models.CampoRepresentationModelAssembler;
import devs.mrp.gullproject.service.CampoService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/campos/id", produces = MediaType.APPLICATION_JSON_VALUE)
public class CampoByIdRestController {
	
	private final CampoService campoService;
	private final CampoRepresentationModelAssembler crma;
	
	@Autowired
	public CampoByIdRestController(CampoService campoService, CampoRepresentationModelAssembler crma) {
		this.campoService = campoService;
		this.crma = crma;
	}
	
	@GetMapping(path = "/{id}")
	public Mono<CampoRepresentationModel> getCampoById(@PathVariable(value = "id") String id) {
		Mono<Campo<?>> campo = campoService.findById(id);
		Mono<CampoRepresentationModel> crm = campo.map(e -> crma.toModel(e));
		return crm;
	}
	
}
