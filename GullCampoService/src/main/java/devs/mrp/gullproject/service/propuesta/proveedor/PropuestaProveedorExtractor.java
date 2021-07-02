package devs.mrp.gullproject.service.propuesta.proveedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFilter;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.TipoPropuesta;
import reactor.core.publisher.Flux;

@Service
public class PropuestaProveedorExtractor implements MyFilter<Flux<Propuesta>, Flux<PropuestaProveedor>> {

	@Autowired  FromPropuestaToProveedorFactory factory;
	
	@Override
	public Flux<PropuestaProveedor> filter(Flux<Propuesta> input) {
		return input.filter(p -> p.getTipoPropuesta().equals(TipoPropuesta.PROVEEDOR)).map(p -> factory.from(p));
	}
	
}
