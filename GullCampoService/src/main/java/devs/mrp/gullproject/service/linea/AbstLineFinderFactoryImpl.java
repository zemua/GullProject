package devs.mrp.gullproject.service.linea;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;

@Service
public class AbstLineFinderFactoryImpl implements AbstLineFinderFactory {

	@Override
	public AbstLineFinder from(List<LineaAbstracta> lineas) {
		return new AbstLineFinderImpl(lineas);
	}

}
