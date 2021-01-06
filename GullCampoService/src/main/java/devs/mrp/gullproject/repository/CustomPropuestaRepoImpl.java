package devs.mrp.gullproject.repository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import reactor.core.publisher.Mono;

public class CustomPropuestaRepoImpl implements CustomPropuestaRepo {

	private final ReactiveMongoTemplate mongoTemplate;
	
	@Autowired
	public CustomPropuestaRepoImpl(ReactiveMongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public Mono<Propuesta> addLinea(String idPropuesta, Linea linea) {
		// TODO TEST
		
		if (linea.getId().equals("") || linea.getId().equals(null)) {
			linea.setId(new ObjectId().toString());
		}
		
		Query query = new Query(Criteria.where("id").is(idPropuesta));
		Update update = new Update().addToSet("lineas", linea);
		return mongoTemplate.findAndModify(query, update, Propuesta.class);

	}

	@Override
	public Mono<Propuesta> removeLinea(String idPropuesta, Linea linea) {
		// TODO TEST
		
		Query query = new Query(Criteria.where("id").is(idPropuesta));
		Update update = new Update().pull("lineas", linea);
		return mongoTemplate.findAndModify(query, update, Propuesta.class);
		
	}

}
