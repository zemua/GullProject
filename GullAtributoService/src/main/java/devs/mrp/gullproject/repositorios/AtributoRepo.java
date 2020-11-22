package devs.mrp.gullproject.repositorios;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import devs.mrp.gullproject.domains.Atributo;

public interface AtributoRepo extends ReactiveMongoRepository<Atributo, String> {

	
	
}
