package devs.mrp.gullproject.service.linea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;

public class AbstLineFinderImpl implements AbstLineFinder {

	private List<LineaAbstracta> lineas;
	private Map<String, List<LineaAbstracta>> byCounterMap;
	
	public AbstLineFinderImpl(List<LineaAbstracta> lineas) {
		this.lineas = lineas;
		byCounterMap = new HashMap<>();
	}
	
	@Override
	public List<LineaAbstracta> findBy(String counterLineId) {
		List<LineaAbstracta> resultado;
		if (!byCounterMap.containsKey(counterLineId)) {
			resultado = this.lineas.stream().filter(l -> l.getCounterLineId() != null && l.getCounterLineId().equals(counterLineId)).collect(Collectors.toList());
			byCounterMap.put(counterLineId, resultado);
		} else {
			resultado = byCounterMap.get(counterLineId);
		}
		return resultado;
	}

}
