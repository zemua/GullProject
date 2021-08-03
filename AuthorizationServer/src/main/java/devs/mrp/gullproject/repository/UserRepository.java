package devs.mrp.gullproject.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.models.MyUser;

@Repository
public interface UserRepository extends MongoRepository<MyUser, String> {

	public MyUser findByUsername(String username);
	
}
