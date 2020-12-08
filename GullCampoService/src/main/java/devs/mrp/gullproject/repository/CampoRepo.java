package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Campo;
import reactor.core.publisher.Flux;

@Repository
public interface CampoRepo extends ReactiveMongoRepository<Campo<?>, String> {
	
}
