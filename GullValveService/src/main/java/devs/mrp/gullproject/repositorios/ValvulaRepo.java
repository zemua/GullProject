package devs.mrp.gullproject.repositorios;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Valvula;

@Repository
public interface ValvulaRepo extends ReactiveMongoRepository<Valvula, String> {

}
