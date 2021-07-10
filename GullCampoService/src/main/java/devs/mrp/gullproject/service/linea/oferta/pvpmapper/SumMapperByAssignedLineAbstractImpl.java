package devs.mrp.gullproject.service.linea.oferta.pvpmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.service.linea.AbstLineFinder;

public class SumMapperByAssignedLineAbstractImpl implements SumMapperByAssignedLineAbstract { // TODO test

	private PropuestaNuestra propuestaNuestra;
	private AbstLineFinder abstLineFinder;
	private Map<String, List<String>> pvpsBySumId;
	
	public SumMapperByAssignedLineAbstractImpl(PropuestaNuestra propuestaNuestra, AbstLineFinder abstLineFinder) {
		this.propuestaNuestra = propuestaNuestra;
		this.abstLineFinder = abstLineFinder;
		this.pvpsBySumId = new HashMap<>();
	}
	
	@Override
	public double findBy(String counterLineid, String sumId) {
		var lines = abstLineFinder.findBy(counterLineid);
		var pvps = pvpsOfSum(sumId);
		return getSum(lines, pvps);
	}
	
	private List<String> pvpsOfSum(String sumId){
		List<String> resultado;
		if (!pvpsBySumId.containsKey(sumId)) {
			var sum = propuestaNuestra.getSums().stream().filter(s -> s.getId().equals(sumId)).findFirst();
			if (sum.isPresent()) {
				resultado = sum.get().getPvperIds();
			} else {
				resultado = new ArrayList<>();
			}
			pvpsBySumId.put(sumId, resultado);
		} else {
			resultado = pvpsBySumId.get(sumId);
		}
		return resultado;
	}
	
	private double getSum(List<LineaAbstracta> lineas, List<String> pvpIds) {
		DoubleAdder resultado = new DoubleAdder();
		lineas.forEach(l -> {
			if (pvpIds.contains(l.getPvp().getPvperId())) {
				resultado.add(l.getPvp().getPvp());
			}
		});
		return resultado.doubleValue();
	}

}
