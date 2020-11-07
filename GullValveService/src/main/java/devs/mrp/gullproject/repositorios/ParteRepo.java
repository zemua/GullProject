package devs.mrp.gullproject.repositorios;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import devs.mrp.gullproject.domains.Parte;

public interface ParteRepo extends ReactiveMongoRepository<Parte, String> {

}
