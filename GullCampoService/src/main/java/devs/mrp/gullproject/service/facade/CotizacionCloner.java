package devs.mrp.gullproject.service.facade;

import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import reactor.core.publisher.Mono;

public interface CotizacionCloner {

	public Mono<PropuestaProveedor> clone(String propClientId, String cotizacionId);
	
}
