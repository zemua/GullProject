package devs.mrp.gullproject.repositorios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.Tipo;
import reactor.core.publisher.Mono;

public class CustomAtributoRepoImpl implements CustomAtributoRepo {

	private final ReactiveMongoTemplate mongoTemplate;
	
	@Autowired
	public CustomAtributoRepoImpl(ReactiveMongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public Mono<Atributo> addAtributo(String id, Tipo tipo) {
		
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().addToSet("tipos", tipo);
		return mongoTemplate.findAndModify(query, update, Atributo.class);
		
	}

	@Override
	public Mono<Atributo> removeAtributo(String id, Tipo tipo) {
		
		Query query = new Query(Criteria.where("id").is(id));
		// Update update = new Update().pull("tipos", new BasicDBObject("nombre", "un nombre"));
		Update update = new Update().pull("tipos", tipo);
		return mongoTemplate.findAndModify(query, update, Atributo.class);
		
	}

}
