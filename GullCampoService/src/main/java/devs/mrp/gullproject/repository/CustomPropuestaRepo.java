package devs.mrp.gullproject.repository;

import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import reactor.core.publisher.Mono;

public interface CustomPropuestaRepo {

	public Mono<Propuesta> addLinea(String id, Linea linea);
	
	public Mono<Propuesta> removeLinea(String id, Linea linea);
	
}
