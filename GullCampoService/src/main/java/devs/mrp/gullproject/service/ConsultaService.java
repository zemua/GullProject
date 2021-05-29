package devs.mrp.gullproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.repository.ConsultaRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ConsultaService {

	ConsultaRepo consultaRepo;
	
	@Autowired
	public ConsultaService(ConsultaRepo consultaRepo) {
		this.consultaRepo = consultaRepo;
	}
	
	public Mono<Consulta> save(Consulta consulta) {
		return consultaRepo.save(consulta);
	}
	
	public Flux<Consulta> findAll() {
		return consultaRepo.findAll();
	}
	
	public Mono<Consulta> findById(String id) {
		return consultaRepo.findById(id);
	}
	
	public Mono<Consulta> addPropuesta(String idConsulta, Propuesta propuesta) {
		return consultaRepo.addPropuesta(idConsulta, propuesta);
	}
	
	public Mono<Long> deleteById(String id){
		return consultaRepo.deleteByIdReturningDeletedCount(id);
	}
	
	public Mono<Consulta> removePropuesta(String idConsulta, Propuesta propuesta){
		return consultaRepo.removePropuesta(idConsulta, propuesta);
	}
	
	public Mono<Integer> removePropuestaById(String idConsulta, String idPropuesta){
		Mono<Consulta> original = findById(idConsulta);
		Mono<Propuesta> p = original.flatMap(cons -> Mono.just(cons.getPropuestaById(idPropuesta)));
		Mono<Consulta> c = p.flatMap(pro -> removePropuesta(idConsulta, pro));
		Mono<Integer> deleted = c.zipWith(original, (item1, item2) -> item2.getCantidadPropuestas() - item1.getCantidadPropuestas());
		return deleted;
	}
	
	public Mono<Propuesta> findPropuestaByPropuestaId(String propuestaId) {
		Mono<Propuesta> mono = consultaRepo.findByPropuestaId(propuestaId).flatMap(c -> Mono.just(c.getPropuestaById(propuestaId)));
		return mono;
	}
	
	public Mono<Consulta> findConsultaByPropuestaId(String propuestaId) {
		return consultaRepo.findByPropuestaId(propuestaId);
	}
	
	public Flux<AtributoForCampo> findAttributesByPropuestaId(String propuestaId) {
		Flux<AtributoForCampo> flux = findPropuestaByPropuestaId(propuestaId).flatMapMany(prop -> Flux.fromIterable(prop.getAttributeColumns()));
		return flux;
	}
	
	public Mono<Consulta> updateAttributesOfPropuesta(String idPropuesta, List<AtributoForCampo> attributes) {
		return consultaRepo.updateAttributesOfPropuesta(idPropuesta, attributes);
	}
	
	public Mono<Consulta> updateNameAndStatus(String idConsulta, String name, String status) {
		return consultaRepo.updateName(idConsulta, name).flatMap(c -> consultaRepo.updateStatus(idConsulta, status));
	}
	
	public Mono<Consulta> updateNombrePropuesta(Propuesta propuesta) { // TODO test
		return consultaRepo.findByPropuestaId(propuesta.getId()).flatMap(rConsulta -> {
			return consultaRepo.updateNombrePropuesta(rConsulta.getId(), propuesta);
		});
	}
}
