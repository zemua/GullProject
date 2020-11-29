package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModel;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModelAssembler;
import devs.mrp.gullproject.repositorios.AtributoRepo;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api/atributos", produces = MediaType.APPLICATION_JSON_VALUE)
public class AtributoRestController {

	private final AtributoService atributoService;
	private final AtributoRepresentationModelAssembler arma;
	
	@Autowired
	public AtributoRestController(AtributoService atributoService, AtributoRepresentationModelAssembler arma) {
		this.atributoService = atributoService;
		this.arma = arma;
	}
	
	@GetMapping(path = "/all")
	public Flux<AtributoRepresentationModel> getAllAtributos() {
		Flux<Atributo> atributos = atributoService.findAll();
		Flux<AtributoRepresentationModel> arm = atributos.map(e -> arma.toModel(e));
		
		return arm;
	}
	
}
