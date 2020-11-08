package devs.mrp.gullproject.repositorios;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import devs.mrp.gullproject.domains.Material;
import reactor.core.publisher.Flux;

public interface MaterialRepo extends ReactiveMongoRepository<Material, String> {
	
	Flux<Material> findAllByName(String name);
}
