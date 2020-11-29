package devs.mrp.gullproject.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import reactor.core.publisher.Mono;

public class CustomLineaRepoImpl implements CustomLineaRepo {

	private final ReactiveMongoTemplate mongoTemplate;
	
	@Autowired
	public CustomLineaRepoImpl(ReactiveMongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public Mono<Linea> addCampo(String id, Campo campo) {
		
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().addToSet("campos", campo);
		return mongoTemplate.findAndModify(query, update, Linea.class);
		
	}

	@Override
	public Mono<Linea> removeCampo(String id, Campo campo) {
		
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().pull("campos", campo);
		return mongoTemplate.findAndModify(query, update, Linea.class);
		
	}

}
