package devs.mrp.gullproject.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CustomConsultaRepoImpl implements CustomConsultaRepo {

	private final ReactiveMongoTemplate mongoTemplate;
	
	@Autowired
	public CustomConsultaRepoImpl(ReactiveMongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public Mono<Consulta> addPropuesta(String idConsulta, Propuesta propuesta) {
		Query query = new Query(Criteria.where("id").is(idConsulta));
		Update update = new Update().addToSet("propuestas", propuesta);
		return mongoTemplate.findAndModify(query, update, Consulta.class);
	}
	
	@Override
	public Mono<Long> addVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas) {
		Query query = new Query(Criteria.where("id").is(idConsulta));
		return propuestas.flatMap(c -> mongoTemplate.findAndModify(
				query,
				new Update().addToSet("propuestas", c),
				Consulta.class))
			.count();
	}

	@Override
	public Mono<Consulta> removePropuesta(String idConsulta, Propuesta propuesta) {
		Query query = new Query(Criteria.where("id").is(idConsulta));
		Update update = new Update().pull("propuestas", propuesta);
		return mongoTemplate.findAndModify(query, update, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> removeVariasPropuestas(String idConsulta, Propuesta[] propuestas) {
		Query query = new Query(Criteria.where("id").is(idConsulta));
		Update update = new Update().pullAll("propuestas", propuestas);
		return mongoTemplate.findAndModify(query, update, Consulta.class);
	}

	@Override
	public Mono<Consulta> updateNombrePropuesta(String idConsulta, Propuesta propuesta) {
		Query query = new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(propuesta.getId()));
		Update update = new Update().set("propuestas.$.nombre", propuesta.getNombre());
		return mongoTemplate.findAndModify(query, update, Consulta.class);
	}
	
	@Override
	public Mono<Long> updateNombreVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas) {
		return propuestas.flatMap(c -> mongoTemplate.findAndModify(
				new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(c.getId())),
				new Update().set("propuestas.$.nombre", c.getNombre()),
				Consulta.class))
			.count();
	}
	
	@Override
	public Mono<Consulta> updateLineasDeUnaPropuesta(String idConsulta, Propuesta propuesta) {
		Query query = new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(propuesta.getId()));
		Update update = new Update().set("propuestas.$.lineas", propuesta.getAllLineas());
		return mongoTemplate.findAndModify(query, update, Consulta.class);
	}
	
	@Override
	public Mono<Long> updateLineasDeVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas) {
		return propuestas.flatMap(c -> mongoTemplate.findAndModify(
				new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(c.getId())),
				new Update().set("propuestas.$.lineas", c.getAllLineas()),
				Consulta.class))
			.count();
	}
	
	@Override
	public Mono<Consulta> updateUnaPropuesta(String idConsulta, Propuesta propuesta) {
		Query query = new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(propuesta.getId()));
		Update update = new Update().set("propuestas.$", propuesta);
		return mongoTemplate.findAndModify(query, update, Consulta.class);
	}
	
	@Override
	public Mono<Long> updateVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas) {
		return propuestas.flatMap(c -> mongoTemplate.findAndModify(
				new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(c.getId())),
				new Update().set("propuestas.$", c),
				Consulta.class))
			.count();
	}
	
	@Override
	public Mono<Consulta> addLineaEnPropuesta(String idConsulta, String idPropuesta, Linea linea) {
		// TODO test
		return null;
	}
	
}
