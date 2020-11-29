package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModel;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModelAssembler;
import devs.mrp.gullproject.repositorios.AtributoRepo;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/atributos/id", produces = MediaType.APPLICATION_JSON_VALUE)
public class AtributoRestControllerById {
	
	private final AtributoService atributoService;
	private final AtributoRepresentationModelAssembler arma;
	
	@Autowired
	public AtributoRestControllerById(AtributoService atributoService, AtributoRepresentationModelAssembler arma) {
		this.atributoService = atributoService;
		this.arma = arma;
	}
	
	@GetMapping(path = "/{id}")
	public Mono<AtributoRepresentationModel> getAtributoById(@PathVariable(value = "id") String id) {
		Mono<Atributo> atributo = atributoService.findById(id);
		Mono<AtributoRepresentationModel> arm = atributo.map(e -> arma.toModel(e));
		return arm;
	}

}
