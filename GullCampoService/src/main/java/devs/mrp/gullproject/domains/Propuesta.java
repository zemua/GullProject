package devs.mrp.gullproject.domains;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

public interface Propuesta {

	public String getId();
	public void setId(String id);
	
	public String getNombre();
	public void setNombre(String nombre);
	
	public boolean isRoot(); // si es o no consulta "original" (del cliente) o falso si es otro caso
	public boolean isForBid(); // verdadero si es "nuestra oferta" para enviar al cliente
	
	public void addLineaId(String lineaId);
	public void addLineaIds(List<String> lineaIds);
	public int getCantidadLineaIds();
	public boolean removeLineaId(String lineaId);
	public void removeLineaIds(List<String> lineaIds);
	public List<String> getAllLineaIds();
	public String getLineaIdByIndex(int index);
	public boolean updateLineaId(String idOriginal, String idDeseado);
	public boolean updateLineaIdByIndex(int index, String lineaId);
	//public boolean saveOrder(Map<String, Integer> idlineaVSposicion); // Integer = posición-de-la-línea, String = id-de-linea
	
	public static boolean confirmaIguales(Linea linea1, Linea linea2) {
		
		if (linea1.getCantidadCampos() != linea2.getCantidadCampos()) {
			return false;
		}
		
		Map<String, Object> map1 = linea1.getCampos().stream().collect(Collectors.toMap(Campo::getAtributoId, Campo::getDatos));
		Map<String, Object> map2 = linea2.getCampos().stream().collect(Collectors.toMap(Campo::getAtributoId, Campo::getDatos));
		
		if(map1.size() != map2.size()) { // Si algún atributoId está repetido por error...
			return false;
		}
		
		Set<String> set1 = map1.keySet();
		Set<String> set2 = map2.keySet();
		if (!set2.containsAll(set1)) {
			return false;
		}
		
		Iterator<String> it = set1.iterator();
		while(it.hasNext()) {
			String s = it.next();
			if (!map1.get(s).equals(map2.get(s))) {
				return false;
			}
		}
		
		return true;
	}
	public static boolean confirmaIgualesSegunCampos(Linea linea1, Linea linea2, List<AtributoForCampo> atributos) {
				Map<String, Object> map1 = linea1.getCampos().stream().collect(Collectors.toMap(Campo::getAtributoId, Campo::getDatos));
				Map<String, Object> map2 = linea2.getCampos().stream().collect(Collectors.toMap(Campo::getAtributoId, Campo::getDatos));
				
				Iterator<AtributoForCampo> it = atributos.iterator();
				while (it.hasNext()) {
					AtributoForCampo a = it.next();
					Object b = map1.get(a.getId());
					Object c = map2.get(a.getId());
					if(!b.getClass().equals(c.getClass())
							|| !b.equals(c)) {
						return false;
					}
				}
				
				return true;
	}
	public static boolean assertMatchesCampos(Linea l, Map<String, ?> atributoidVSvalue) {
		
		Map<String, Object> mapLinea = l.getCampos().stream().collect(Collectors.toMap(Campo::getAtributoId, Campo::getDatos));

		Set<String> atributoIds = atributoidVSvalue.keySet();

		Iterator<String> iterator = atributoIds.iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();

			if(mapLinea.containsKey(key) && (!mapLinea.get(key).equals(atributoidVSvalue.get(key)))) {
				return false;
			} else if (!mapLinea.containsKey(key)) {
				if (atributoidVSvalue.get(key).getClass().equals(String.class)
						&& (!atributoidVSvalue.get(key).equals(""))) {
					return false;
				} else if (atributoidVSvalue.get(key).getClass().equals(Integer.class)
						&& (!atributoidVSvalue.get(key).equals(Integer.valueOf(0)))) {
					return false;
				} else if (atributoidVSvalue.get(key).getClass().equals(Long.class)
						&& (!atributoidVSvalue.get(key).equals(Long.valueOf(0)))) {
					return false;
				} else if (atributoidVSvalue.get(key).getClass().equals(Double.class)
						&& (!atributoidVSvalue.get(key).equals(Double.valueOf(0)))) {
					return false;
				} else if (atributoidVSvalue.get(key).getClass().equals(Float.class)
						&& (!atributoidVSvalue.get(key).equals(Float.valueOf(0)))) {
					return false;
				} else if (atributoidVSvalue.get(key).getClass().equals(Boolean.class)
						&& (!atributoidVSvalue.get(key).equals(Boolean.valueOf(false)))) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static boolean assertMatchesCamposEstricto(Linea l, Map<String, ?> atributoidVSvalue) {

		Map<String, Object> mapLinea = l.getCampos().stream().collect(Collectors.toMap(Campo::getAtributoId, Campo::getDatos));

		Set<String> atributoIds = atributoidVSvalue.keySet();
		Set<String> lineaKeys = mapLinea.keySet();

		if (!lineaKeys.containsAll(atributoIds)) {
			return false;
		}
		
		Iterator<String> iterator = atributoIds.iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			
			if (!mapLinea.containsKey(key)
					|| !atributoidVSvalue.get(key).getClass().equals(mapLinea.get(key).getClass())
					|| !atributoidVSvalue.get(key).equals(mapLinea.get(key))) {
				return false;
			}
		}

		return true;
	}

}