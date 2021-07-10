package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import reactor.core.publisher.Flux;

@Repository
public interface LineaOfertaRepo extends ReactiveMongoRepository<LineaAbstracta, String> {

	Flux<LineaAbstracta> findAllByPropuestaId(String propuestaId);
	
}
