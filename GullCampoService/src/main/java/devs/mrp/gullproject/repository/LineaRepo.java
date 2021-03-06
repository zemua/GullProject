package devs.mrp.gullproject.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.linea.Linea;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LineaRepo extends ReactiveMongoRepository<Linea, String>, CustomLineaRepo {
	
	// throws error with security
	//@DeleteQuery("{_id:?0}")
	//Mono<Long> deleteByIdReturningDeletedCount(String id);
	
	// throws error with security
	//@DeleteQuery("{ 'propuestaId' : ?0 }")
	//Mono<Long> deleteByPropuestaIdReturningDeletedCount(String propuestaId);
	
	Flux<Linea> findAllByPropuestaId(String propuestaId);
	
	Flux<Linea> findLineasByIdIn(List<String> ids);
	Flux<Linea> findLineasByPropuestaIdIn(List<String> propuestaIds);
	
	Flux<Linea> findLineasByIdInOrderByOrderAsc(List<String> ids);
	Flux<Linea> findAllByPropuestaIdOrderByOrderAsc(String propuestaId);
	Flux<Linea> findAllByOrderByOrderAsc();
	Flux<Linea> findAllByOrderByOrderDesc();
	
	Mono<Long> countByPropuestaId(String propuestaId);
	
	Mono<Linea> findFirstByPropuestaIdOrderByOrderDesc(String propuestaId);
	
}
