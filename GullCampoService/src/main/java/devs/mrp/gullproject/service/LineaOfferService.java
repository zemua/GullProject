package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.LineaAbstracta;
import devs.mrp.gullproject.repository.LineaOfertaRepo;
import reactor.core.publisher.Flux;

@Service
public class LineaOfferService {

	@Autowired LineaOfertaRepo repo;
	
	public Flux<LineaAbstracta> findByPropuesta(String propuestaId) {
		return repo.findAllByPropuestaId(propuestaId);
	}
	
}
