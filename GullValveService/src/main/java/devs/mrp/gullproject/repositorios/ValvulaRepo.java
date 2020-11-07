package devs.mrp.gullproject.repositorios;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import devs.mrp.gullproject.domains.Valvula;

public interface ValvulaRepo extends ReactiveMongoRepository<Valvula, String> {

}
