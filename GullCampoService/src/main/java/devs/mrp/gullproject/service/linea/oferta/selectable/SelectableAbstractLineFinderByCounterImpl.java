package devs.mrp.gullproject.service.linea.oferta.selectable;

import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;

public class SelectableAbstractLineFinderByCounterImpl implements SelectableAbstractLineFinderByCounter {

	List<SelectableAbstractLine> lineas;
	
	public SelectableAbstractLineFinderByCounterImpl(List<SelectableAbstractLine> lineas) {
		this.lineas = lineas;
	}
	
	@Override
	public List<SelectableAbstractLine> find(String counterLineId) {
		return lineas.stream().filter(l -> l.getCounterLineId().equals(counterLineId)).collect(Collectors.toList());
	}

}
