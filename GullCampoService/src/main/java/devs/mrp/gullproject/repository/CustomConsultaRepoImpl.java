package devs.mrp.gullproject.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Consulta;
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
	public Mono<Consulta> findByPropuestaId(String propuestaId) {
		Query query = new Query(Criteria.where("propuestas.id").is(propuestaId));
		return mongoTemplate.findOne(query, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> addPropuesta(String idConsulta, Propuesta propuesta) {
		Query query = new Query(Criteria.where("id").is(idConsulta));
		Update update = new Update().addToSet("propuestas", propuesta);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
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
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> removeVariasPropuestas(String idConsulta, Propuesta[] propuestas) {
		Query query = new Query(Criteria.where("id").is(idConsulta));
		Update update = new Update().pullAll("propuestas", propuestas);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}

	@Override
	public Mono<Consulta> updateNombrePropuesta(String idConsulta, Propuesta propuesta) {
		Query query = new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(propuesta.getId()));
		Update update = new Update().set("propuestas.$.nombre", propuesta.getNombre());
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
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
		Update update = new Update().set("propuestas.$.lineaIds", propuesta.getAllLineaIds());
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Long> updateLineasDeVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas) {
		return propuestas.flatMap(c -> mongoTemplate.findAndModify(
				new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(c.getId())),
				new Update().set("propuestas.$.lineaIds", c.getAllLineaIds()),
				Consulta.class))
			.count();
	}
	
	@Override
	public Mono<Consulta> updateUnaPropuesta(String idConsulta, Propuesta propuesta) {
		Query query = new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(propuesta.getId()));
		Update update = new Update().set("propuestas.$", propuesta);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
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
	public Mono<Consulta> addLineaEnPropuesta(String idConsulta, String idPropuesta, String idLinea) {
		Query query = new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(idPropuesta));
		Update update = new Update().addToSet("propuestas.$.lineaIds", idLinea);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> removeLineaEnPropuesta(String idConsulta, String idPropuesta, String idLinea) {
		Query query = new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(idPropuesta));
		Update update = new Update().pull("propuestas.$.lineaIds", idLinea);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> updateStatus(String idConsulta, String status) {
		Query query = new Query(Criteria.where("id").is(idConsulta));
		Update update = new Update().set("status", status);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> addAttributeToList(String idPropuesta, AtributoForCampo attribute){
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().addToSet("propuestas.$.atributeColumns", attribute);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> removeAttributeFromList(String idPropuesta, AtributoForCampo attribute) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().pull("propuestas.$.atributeColumns", attribute);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> updateAttributesOfPropuesta(String idPropuesta, List<AtributoForCampo> attributes) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().set("propuestas.$.atributeColumns", attributes);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
}
