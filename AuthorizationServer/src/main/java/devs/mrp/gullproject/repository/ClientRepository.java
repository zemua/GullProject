package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.models.MyClient;

@Repository
public interface ClientRepository extends MongoRepository<MyClient, String> {

	public MyClient findByClientName(String clientName);
	
}
