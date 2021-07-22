package devs.mrp.gullproject.service.propuesta.proveedor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;

public class CotizacionOfCostMapperImpl implements CotizacionOfCostMapper {

	private Map<String, String> costIdVsCotizacionId;
	
	public CotizacionOfCostMapperImpl(Consulta consulta) {
		costIdVsCotizacionId = new HashMap<>();
		initMap(consulta);
	}
	
	@Override
	public String of(String costId) {
		return costIdVsCotizacionId.get(costId);
	}
	
	private void initMap(Consulta consulta) {
		List<PropuestaProveedor> propuestas = new ArrayList<>();
		consulta.operations().getPropuestasProveedor().stream().forEach(p -> {
			propuestas.add((PropuestaProveedor)p);
		});
		
		propuestas.forEach(p -> {
			p.getCostes().forEach(c -> {
				costIdVsCotizacionId.put(c.getId(), p.getId());
			});
		});
	}

}
