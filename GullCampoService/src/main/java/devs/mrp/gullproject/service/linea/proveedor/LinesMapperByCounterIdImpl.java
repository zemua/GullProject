package devs.mrp.gullproject.service.linea.proveedor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.gullproject.domains.linea.Linea;

public class LinesMapperByCounterIdImpl implements LinesMapperByCounterId { // TODO test

	private List<Linea> lineas;
	private Map<String, List<Linea>> map;
	
	public LinesMapperByCounterIdImpl(List<Linea> lineas) {
		this.lineas = lineas;
		map = new HashMap<>();
	}
	
	@Override
	public List<Linea> forCounter(String counterId) {
		List<Linea> resultado;
		if (map.containsKey(counterId)) {
			resultado = map.get(counterId);
		} else {
			resultado = lineas.stream().filter(l -> l.getCounterLineId().contains(counterId)).collect(Collectors.toList());
			map.put(counterId, resultado);
		}
		return resultado;
	}

}
