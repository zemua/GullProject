package devs.mrp.gullproject.service.linea.oferta.selectable;

import java.util.List;

import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;

public interface SelectableAbstractLineFinderByCounterFactory {

	public SelectableAbstractLineFinderByCounter from(List<SelectableAbstractLine> lineas);
	
}
