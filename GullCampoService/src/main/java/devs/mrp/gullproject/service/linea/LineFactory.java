package devs.mrp.gullproject.service.linea;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryCopy;
import devs.mrp.gullproject.ainterfaces.MyFactoryNew;
import devs.mrp.gullproject.domains.linea.Linea;

@Service
public class LineFactory implements MyFactoryNew<Linea> {

	@Override
	public Linea create() {
		Linea l = new Linea();
		l.setCampos(new ArrayList<>());
		return l;
	}

}
