package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Propuesta;

@Repository
public interface PropuestaRepo extends ReactiveMongoRepository<Propuesta, String>, CustomPropuestaRepo {

}
