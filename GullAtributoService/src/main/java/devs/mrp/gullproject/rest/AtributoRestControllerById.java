package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.representationmodels.AtributoRespresentationModelMapper;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/atributos/id", produces = MediaType.APPLICATION_JSON_VALUE)
public class AtributoRestControllerById {
	
	private final AtributoService atributoService;
	//private final AtributoRepresentationModelAssembler arma;
	
	@Autowired AtributoRespresentationModelMapper repMapper;
	
	@Autowired
	public AtributoRestControllerById(AtributoService atributoService) {
		this.atributoService = atributoService;
	}
	
	@GetMapping(path = "/{id}")
	public Mono<EntityModel<Atributo>> getAtributoById(@PathVariable(value = "id") String id) {
		Mono<Atributo> atributo = atributoService.findById(id);
		Mono<EntityModel<Atributo>> arm = atributo.flatMap(e -> repMapper.from(e));
		return arm;
	}

}
