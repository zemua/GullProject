package devs.mrp.gullproject.service.linea;

import java.util.List;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;

public interface AbstLineFinderFactory {

	public AbstLineFinder from(List<LineaAbstracta> lineas);
	
}
