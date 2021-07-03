package devs.mrp.gullproject.domains.linea;

import devs.mrp.gullproject.ainterfaces.MyFactoryNew;

public class LineaFactory implements MyFactoryNew<Linea> {

	@Override
	public Linea create() {
		return new Linea();
	}

}
