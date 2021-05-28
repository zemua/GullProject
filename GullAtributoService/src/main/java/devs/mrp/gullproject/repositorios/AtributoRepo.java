package devs.mrp.gullproject.repositorios;

import java.util.List;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Atributo;
import reactor.core.publisher.Flux;

@Repository
public interface AtributoRepo extends ReactiveMongoRepository<Atributo, String>, CustomAtributoRepo {
	public Flux<Atributo> findAtributosByIdIn(List<String> ids);
}
