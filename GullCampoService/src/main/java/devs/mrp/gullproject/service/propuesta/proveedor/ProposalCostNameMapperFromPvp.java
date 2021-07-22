package devs.mrp.gullproject.service.propuesta.proveedor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.gullproject.ainterfaces.IdName;
import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;

public class ProposalCostNameMapperFromPvp implements MyFinder<List<IdName>, String> {

	private PropuestaNuestra oferta;
	private List<PropuestaProveedor> propuestas;
	private Map<String, IdName> costesMap;
	private Map<String, List<IdName>> pvpsMap;
	
	public ProposalCostNameMapperFromPvp(PropuestaNuestra oferta, List<PropuestaProveedor> propuestas) {
		this.oferta = oferta;
		this.propuestas = propuestas;
		init();
	}
	
	@Override
	public List<IdName> findBy(String pvpId) {
		return pvpsMap.get(pvpId);
	}
	
	private void init() {
		costesMap = new HashMap<>();
		propuestas.forEach(p -> {
			p.getCostes().forEach(c -> {
				costesMap.put(c.getId(), new Pair(c.getId(),c.getName()));
			});
		});
		
		pvpsMap = new HashMap<>();
		oferta.getPvps().forEach(pvp -> {
			List<IdName> list = new ArrayList<>();
			pvp.getIdCostes().forEach(c -> {
				list.add(costesMap.get(c));
			});
			pvpsMap.put(pvp.getId(), list);
		});
	}
	
	private class Pair implements IdName {
		String id;
		String name;
		public Pair(String i, String n) {id = i;name = n;}
		@Override
		public String getId() {return id;}
		@Override
		public String getName() {return name;}
		public void setId(String setId) {id = setId;}
		public void setName(String setName) {name = setName;}
	}

}
