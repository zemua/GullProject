package devs.mrp.gullproject.service.linea.oferta.selectable;

import java.util.List;

import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;

public class SelectableLineMapperByCounterAndPvpImpl implements SelectableLineMapperByCounterAndPvp {

	private SelectableAbstractLineFinderByCounter finder;
	
	public SelectableLineMapperByCounterAndPvpImpl(List<SelectableAbstractLine> lineas, SelectableAbstractLineFinderByCounterFactory finderFactory) {
		finder = finderFactory.from(lineas);
	}
	
	@Override
	public SelectableAbstractLine findBy(String counterLineId, String pvpId) {
		List<SelectableAbstractLine> selectables = finder.find(counterLineId);
		return selectables.stream().filter(l -> l.getPvp().getPvperId().equals(pvpId)).findAny().orElse(null);
	}

}
