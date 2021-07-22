package devs.mrp.gullproject.service.linea;

import java.util.List;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;

public interface AbstLineFinder {

	public List<LineaAbstracta> findBy(String counterLineId);
	
}
