package devs.mrp.gullproject.service.linea.oferta;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaOferta;

@Service
public class LineToOfferLineFactory implements MyFactoryFromTo<Linea, LineaOferta> {

	@Override
	public LineaOferta from(Linea linea) {
		if (linea instanceof LineaOferta) {
			return (LineaOferta) linea;
		}
		var l = new LineaOferta(linea);
		l.setId(linea.getId());
		return l;
	}
	
}
