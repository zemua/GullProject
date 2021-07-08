package devs.mrp.gullproject.domainsdto.linea.selectable;

import java.util.List;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;

public interface SelectableLinesWrapBuilder {

	public SelectableLinesWrap from(List<Linea> lineasCliente, List<SelectableAbstractLine> lineasOferta, PropuestaNuestra propuestaNuestra);
	
}
