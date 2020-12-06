package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Campo;

@Repository
public interface CampoRepo extends ReactiveMongoRepository<Campo<?>, String> {
	
}
