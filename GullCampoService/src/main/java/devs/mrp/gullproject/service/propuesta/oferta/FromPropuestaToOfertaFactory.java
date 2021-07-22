package devs.mrp.gullproject.service.propuesta.oferta;

import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;

public interface FromPropuestaToOfertaFactory {

	public PropuestaNuestra from(Propuesta propuesta);
	
}
