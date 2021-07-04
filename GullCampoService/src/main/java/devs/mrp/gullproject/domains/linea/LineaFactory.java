package devs.mrp.gullproject.domains.linea;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryCopy;
import devs.mrp.gullproject.ainterfaces.MyFactoryNew;

@Service
public class LineaFactory implements MyFactoryNew<Linea>, MyFactoryCopy<Linea> {

	@Override
	public Linea create() {
		return new LineaImpl();
	}

	@Override
	public Linea from(Linea element) {
		return new LineaImpl(element);
	}

}
