package devs.mrp.gullproject.domains.representationmodels;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.rest.AtributoRestController;
import devs.mrp.gullproject.rest.AtributoRestControllerById;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.*;

@Service
public class AtributoRespresentationModelMapperImpl implements AtributoRespresentationModelMapper {

	@Override
	public Mono<CollectionModel<Atributo>> from(List<Atributo> entities) {
		var controller = methodOn(AtributoRestController.class);
		return Flux.fromIterable(entities)
				.collectList()
				.flatMap(resources -> linkTo(controller.getAllAtributos()).withSelfRel()
						//.andAffordance(controller.newAttributo(null))
						.toMono()
						.map(selfLink -> CollectionModel.of(resources, selfLink)));
	}
	
	@Override
	public Mono<EntityModel<Atributo>> from(Atributo entity) {
		var controller = methodOn(AtributoRestController.class);
		var idcontroller = methodOn(AtributoRestControllerById.class);
		var todos = linkTo(controller.getAllAtributos()).withRel("all").toMono();
		return linkTo(idcontroller.getAtributoById(entity.getId())).withSelfRel()
			//.andAffordance(controller.getAllAtributos())
			.toMono()
			.flatMap(selfLink -> {
				return todos.map(todosLink -> {
					return EntityModel.of(entity, selfLink, todosLink);
				});
			});
	}

}
