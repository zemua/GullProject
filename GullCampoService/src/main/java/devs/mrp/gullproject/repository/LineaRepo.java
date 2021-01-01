package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Linea;
import reactor.core.publisher.Mono;

@Repository
public interface LineaRepo extends ReactiveMongoRepository<Linea, String>, CustomLineaRepo {
	
	/**
	 * La forma más fácil y eficiente de devolver el número de archivos afectados
	 * https://stackoverflow.com/a/58015168/14101720
	 */
	
	@DeleteQuery("{_id:?0}")
	Mono<Long> deleteByIdReturningDeletedCount(String id);
	
}
