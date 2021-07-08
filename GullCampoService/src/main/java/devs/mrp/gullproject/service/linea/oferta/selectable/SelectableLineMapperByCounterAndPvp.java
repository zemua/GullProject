package devs.mrp.gullproject.service.linea.oferta.selectable;

import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;

public interface SelectableLineMapperByCounterAndPvp {

	public SelectableAbstractLine findBy(String counterLineId, String pvpId);
	
}
