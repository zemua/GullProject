package devs.mrp.gullproject.service.propuesta.oferta;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.propuestas.Pvper;

public interface IncludeCotizacionCheckerFactory {

	public IncludeCotizacionChecker from(Pvper pvper, Consulta consulta);
	
}
