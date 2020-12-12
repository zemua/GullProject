package devs.mrp.gullproject.repositorios;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Atributo;

@Repository
public interface AtributoRepo extends ReactiveMongoRepository<Atributo, String>, CustomAtributoRepo {
	
}
