package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.repository.LineaOfertaRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LineaOfferService {

	@Autowired LineaOfertaRepo repo;
	
	public Flux<LineaAbstracta> findByPropuesta(String propuestaId) {
		return repo.findAllByPropuestaId(propuestaId);
	}
	
	public Mono<LineaAbstracta> addLinea(LineaAbstracta linea) {
		return repo.insert(linea);
	}
	
	public Mono<LineaAbstracta> updateOrAddLinea(LineaAbstracta linea) {
		return repo.save(linea);
	}
	
}
