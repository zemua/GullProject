package devs.mrp.gullproject.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.Pvper;
import devs.mrp.gullproject.domains.PvperSum;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
		log.debug("propuesta to add: " + propuesta.toString());
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
		log.debug("going to remove " + propuesta.toString());
		// Query query = new Query(Criteria.where("id").is(idConsulta));
		Query query = new Query(Criteria.where("propuestas.id").is(propuesta.getId()));
		// Update update = new Update().pull("propuestas", propuesta);
		// Update update = new Update().pull("propuestas", Query.query(Criteria.where("propuesta.id").is(propuesta.getId())));
		Update update = new Update().pull("propuestas", new BasicDBObject("id", propuesta.getId()));
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> removePropuestasByAssignedTo(String idConsulta, String idAssignedTo) {
		Query query = new Query(Criteria.where("propuestas.forProposalId").is(idAssignedTo).and("id").is(idConsulta));
		Update update = new Update().pull("propuestas", new BasicDBObject("forProposalId", idAssignedTo));
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(false); // return old one with references to the proposals to delete lines after that
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
		Update update = new Update().set("propuestas.$.lineaIds", propuesta.operations().getAllLineaIds());
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Long> updateLineasDeVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas) {
		return propuestas.flatMap(c -> mongoTemplate.findAndModify(
				new Query(Criteria.where("id").is(idConsulta).and("propuestas.id").is(c.getId())),
				new Update().set("propuestas.$.lineaIds", c.operations().getAllLineaIds()),
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
	public Mono<Consulta> updateName(String idConsulta, String name) {
		Query query = new Query(Criteria.where("id").is(idConsulta));
		Update update = new Update().set("nombre", name);
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
		Update update = new Update().addToSet("propuestas.$.attributeColumns", attribute);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> removeAttributeFromList(String idPropuesta, AtributoForCampo attribute) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().pull("propuestas.$.attributeColumns", attribute);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> updateAttributesOfPropuesta(String idPropuesta, List<AtributoForCampo> attributes) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().set("propuestas.$.attributeColumns", attributes);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> updateCostesOfPropuesta(String idPropuesta, List<CosteProveedor> costes) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().set("propuestas.$.costes", costes);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> addCostToList(String idPropuesta, CosteProveedor coste) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().addToSet("propuestas.$.costes", coste);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> updatePvpsOfPropuesta(String idPropuesta, List<Pvper> pvps) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().set("propuestas.$.pvps", pvps);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> addPvpToList(String idPropuesta, Pvper pvp) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().addToSet("propuestas.$.pvps", pvp);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> updatePvpSumsOfPropuesta(String idPropuesta, List<PvperSum> sums) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().set("propuestas.$.sums", sums);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
	@Override
	public Mono<Consulta> addPvpSumToList(String idPropuesta, PvperSum sum) {
		Query query = new Query(Criteria.where("propuestas.id").is(idPropuesta));
		Update update = new Update().addToSet("propuestas.$.sums", sum);
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		return mongoTemplate.findAndModify(query, update, options, Consulta.class);
	}
	
}
