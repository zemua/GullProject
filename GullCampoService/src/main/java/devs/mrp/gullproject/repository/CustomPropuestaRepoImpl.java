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
	public Mono<Propuesta> addLinea(String idPropuesta, String lineaId) throws Exception {
		
		if (lineaId.equals("") || lineaId.equals(null)) {
			throw new Exception("linea id is an invalid reference");
		}
		
		Query query = new Query(Criteria.where("id").is(idPropuesta));
		Update update = new Update().addToSet("lineaIds", lineaId);
		return mongoTemplate.findAndModify(query, update, Propuesta.class);

	}

	@Override
	public Mono<Propuesta> removeLinea(String idPropuesta, String lineaId) throws Exception {
		
		if (lineaId.equals("") || lineaId.equals(null)) {
			throw new Exception("linea id is an invalid reference");
		}
		
		Query query = new Query(Criteria.where("id").is(idPropuesta));
		Update update = new Update().pull("lineaIds", lineaId);
		return mongoTemplate.findAndModify(query, update, Propuesta.class);
		
	}

}
