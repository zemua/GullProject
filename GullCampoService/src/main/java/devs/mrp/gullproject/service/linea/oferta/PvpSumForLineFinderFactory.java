package devs.mrp.gullproject.service.linea.oferta;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;

@Service
public class PvpSumForLineFinderFactory implements MyFactoryFromTo<PropuestaNuestra, MyMapperByDupla<Double, Linea, String>> {
	
	@Override
	public MyMapperByDupla<Double, Linea, String> from(PropuestaNuestra propuesta) {
		return new PvpSumForLineFinder(propuesta);
	}

}
