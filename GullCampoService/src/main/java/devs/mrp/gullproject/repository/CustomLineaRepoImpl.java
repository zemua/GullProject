package devs.mrp.gullproject.repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CustomLineaRepoImpl implements CustomLineaRepo {

	private final ReactiveMongoTemplate mongoTemplate;
	
	@Autowired
	public CustomLineaRepoImpl(ReactiveMongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	/**
	 * Query personalizado mongodb
	 * https://dev.to/andreevich/how-to-build-custom-queries-with-spring-data-reactive-mongodb-1802
	 * referencia codigo fuente
	 * https://github.com/mednikoviurii/spring-reactive-examples
	 */
	
	@Override
	public Mono<Linea> addCampo(String id, Campo<?> campo) {
		
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().addToSet("campos", campo);
		return mongoTemplate.findAndModify(query, update, Linea.class);
		
	}

	@Override
	public Mono<Linea> removeCampo(String id, Campo<?> campo) {
		
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().pull("campos", campo);
		return mongoTemplate.findAndModify(query, update, Linea.class);
		
	}

	@Override
	public Mono<Linea> updateCampo(String idLinea, Campo<?> campo) {
		
		/**
		 * https://stackoverflow.com/questions/35611449/mongodb-how-update-element-in-array-using-spring-query-update
		 */
		
		Query query = new Query(Criteria.where("id").is(idLinea).and("campos.id").is(campo.getId()));
		Update update = new Update().set("campos.$.datos", campo.getDatos());
		return mongoTemplate.findAndModify(query, update, Linea.class);
		
	}

	@Override
	public Mono<Long> addVariosCampos(String idLinea, Flux<Campo<?>> campos) {
		Query query = new Query(Criteria.where("id").is(idLinea));
		return campos.flatMap(c -> mongoTemplate.findAndModify(
				query,
				new Update().addToSet("campos", c),
				Linea.class))
			.count();
	}

	@Override
	public Mono<Linea> removeVariosCampos(String idLinea, Campo<?>[] campos) {
		Query query = new Query(Criteria.where("id").is(idLinea));
		Update update = new Update().pullAll("campos", campos);
		return mongoTemplate.findAndModify(query, update, Linea.class);
	}

	@Override
	public Mono<Long> updateVariosCampos(String idLinea, Flux<Campo<?>> campo) {
		return campo.flatMap(c -> mongoTemplate.findAndModify(
				new Query(Criteria.where("id").is(idLinea).and("campos.id").is(c.getId())),
				new Update().set("campos.$.datos", c.getDatos()),
				Linea.class))
			.count();
	}

}
