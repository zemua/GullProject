package devs.mrp.gullproject.repositorios;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import devs.mrp.gullproject.domains.Pieza;

public interface PiezaRepo extends ReactiveMongoRepository<Pieza, String> {

}
