package devs.mrp.gullproject.domains.representationmodels;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import devs.mrp.gullproject.domains.Atributo;
import reactor.core.publisher.Mono;

public interface AtributoRespresentationModelMapper {

	public Mono<CollectionModel<Atributo>> from(List<Atributo> entities);
	
	public Mono<EntityModel<Atributo>> from(Atributo entity);
	
}
