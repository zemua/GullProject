package devs.mrp.gullproject.repository;

import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import reactor.core.publisher.Mono;

public interface CustomPropuestaRepo {

	public Mono<Propuesta> addLinea(String id, String lineaId) throws Exception;
	
	public Mono<Propuesta> removeLinea(String id, String lineaId) throws Exception;
	
}
