package devs.mrp.gullproject.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Atributo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AtributoRepo extends ReactiveMongoRepository<Atributo, String>, CustomAtributoRepo {
	public Flux<Atributo> findAtributosByIdInOrderByOrdenAsc(List<String> ids);
	
	public Flux<Atributo> findAllByOrderByOrdenAsc();
	
	public Mono<Atributo> findFirstByOrderByOrdenDesc();
}
