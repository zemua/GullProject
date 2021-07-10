package devs.mrp.gullproject.domainsdto.linea.selectable;

import java.util.List;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;

public interface SelectableLineFactory {

	public SelectableAbstractLine create();
	
	public SelectableAbstractLine from(LineaAbstracta linea);
	
	public List<SelectableAbstractLine> from(List<LineaAbstracta> lineas);
	
}
