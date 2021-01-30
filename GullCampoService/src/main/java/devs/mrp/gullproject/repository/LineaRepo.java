package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Linea;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LineaRepo extends ReactiveMongoRepository<Linea, String>, CustomLineaRepo {
	
	@DeleteQuery("{_id:?0}")
	Mono<Long> deleteByIdReturningDeletedCount(String id);
	
	@DeleteQuery("{ 'propuestaId' : ?0 }")
	Mono<Long> deleteByPropuestaIdReturningDeletedCount(String propuestaId);
	
	Flux<Linea> findAllByPropuestaId(String propuestaId);
	
}
