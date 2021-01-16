package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Consulta;
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
	
}
