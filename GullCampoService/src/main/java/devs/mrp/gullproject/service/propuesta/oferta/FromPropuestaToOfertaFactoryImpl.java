package devs.mrp.gullproject.service.propuesta.oferta;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;

@Service
public class FromPropuestaToOfertaFactoryImpl implements FromPropuestaToOfertaFactory {

	@Override
	public PropuestaNuestra from(Propuesta propuesta) {
		if (propuesta instanceof PropuestaNuestra) {
			return (PropuestaNuestra)propuesta;
		} else {
			return new PropuestaNuestra(propuesta);
		}
	}

}
