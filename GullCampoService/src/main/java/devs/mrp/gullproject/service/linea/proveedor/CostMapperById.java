package devs.mrp.gullproject.service.linea.proveedor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;

public class CostMapperById implements MyMapperByDupla<Double, String, String> {

	private List<Linea> lineas;
	private Map<String, List<CosteLineaProveedor>> map;
	
	public CostMapperById(List<Linea> lineas) {
		this.lineas = lineas;
		init();
	}
	
	@Override
	public Double getByDupla(String counterLineId, String costId) {
		var costes = map.get(counterLineId);
		CosteLineaProveedor defecto = new CosteLineaProveedor();
		defecto.setValue(0D);
		if (costes != null) {
			return costes.stream().filter(c -> c.getCosteProveedorId().equals(costId)).findAny().orElse(defecto).getValue();
		}
		return 0D;
	}
	
	private void init() {
		map = new HashMap<>();
		lineas.forEach(l -> {
			if (l.getCounterLineId() != null) {
				l.getCounterLineId().forEach(counter -> {
					if (!map.containsKey(counter)) {
						map.put(counter, l.getCostesProveedor());
					} else {
						map.get(counter).addAll(l.getCostesProveedor());
					}
				});
			}
		});
	}
	
}
