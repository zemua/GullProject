package devs.mrp.gullproject.repository;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import reactor.core.publisher.Mono;

public interface CustomLineaRepo {

	public Mono<Linea> addCampo(String id, Campo campo);
	
	public Mono<Linea> removeCampo(String id, Campo campo);
	
}
