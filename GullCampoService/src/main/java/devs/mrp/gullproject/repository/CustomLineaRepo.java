package devs.mrp.gullproject.repository;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import reactor.core.publisher.Mono;

public interface CustomLineaRepo {

	public Mono<Linea> addCampo(String idLinea, Campo<?> campo);
	
	public Mono<Linea> removeCampo(String idLinea, Campo<?> campo);
	
	public Mono<Linea> updateCampo(String idLinea, Campo<?> campo);
	
}
