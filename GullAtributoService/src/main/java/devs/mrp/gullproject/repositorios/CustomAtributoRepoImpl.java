package devs.mrp.gullproject.repositorios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;

import devs.mrp.gullproject.domains.Atributo;
import reactor.core.publisher.Mono;

public class CustomAtributoRepoImpl implements CustomAtributoRepo {

	private final ReactiveMongoTemplate mongoTemplate;
	
	@Autowired
	public CustomAtributoRepoImpl(ReactiveMongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public Mono<Atributo> updateNameOfAtributo(String id, String name) {
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().set("name", name);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update,  options, Atributo.class);
	}
	
	@Override
	public Mono<Atributo> updateOrdenOfAtributo(String id, Integer orden) {
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().set("orden", orden);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Atributo.class);
	}

}
