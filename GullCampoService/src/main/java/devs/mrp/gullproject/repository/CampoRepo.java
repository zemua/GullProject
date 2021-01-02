package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Campo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CampoRepo extends ReactiveMongoRepository<Campo<?>, String> {
	
	@DeleteQuery("{_id:?0}")
	Mono<Long> deleteByIdReturningDeletedCount(String id);
	
}
