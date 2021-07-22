package devs.mrp.gullproject.service.propuesta.oferta;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.propuestas.Pvper;

@Service
public class IncludeCotizacionCheckerFactoryImpl implements IncludeCotizacionCheckerFactory {

	@Override
	public IncludeCotizacionChecker from(Pvper pvper, Consulta consulta) {
		return new IncludeCotizacionCheckerImpl(pvper, consulta);
	}

}
