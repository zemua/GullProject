package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Linea;

@Repository
public interface LineaRepo extends ReactiveMongoRepository<Linea, String>, CustomLineaRepo {
	
}
