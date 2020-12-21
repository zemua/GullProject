package devs.mrp.gullproject.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.models.CampoRepresentationModel;
import devs.mrp.gullproject.domains.models.CampoRepresentationModelAssembler;
import devs.mrp.gullproject.service.CampoService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api/campos", produces = MediaType.APPLICATION_JSON_VALUE)
public class CampoRestController {
	
	private final CampoService campoService;
	private final CampoRepresentationModelAssembler crma;
	
	public CampoRestController(CampoService campoService, CampoRepresentationModelAssembler crma) {
		this.campoService = campoService;
		this.crma = crma;
	}
	
	@GetMapping(path = "/all")
	public Flux<CampoRepresentationModel> getallCampos(){
		Flux<Campo<?>> campos = campoService.findAll();
		Flux<CampoRepresentationModel> crm = campos.map(e -> crma.toModel(e));
		return crm;
	}
	
	

}
