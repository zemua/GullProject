package devs.mrp.gullproject.domains.linea.abs;

import org.springframework.stereotype.Service;

@Service
public class LineaAbstractaFactoryImpl implements LineaAbstractaFactory {

	@Override
	public LineaAbstracta create() {
		return new LineaAbsImpl();
	}

}
