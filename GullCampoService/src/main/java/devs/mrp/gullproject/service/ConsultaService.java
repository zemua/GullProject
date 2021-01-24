package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	public Mono<Consulta> removePropuestaById(String idConsulta, String idPropuesta){
		Mono<Propuesta> p = findById(idConsulta).flatMap(cons -> Mono.just(cons.getPropuestaById(idPropuesta)));
		Mono<Consulta> c = p.flatMap(pro -> removePropuesta(idConsulta, pro));
		return c;
	}
}
