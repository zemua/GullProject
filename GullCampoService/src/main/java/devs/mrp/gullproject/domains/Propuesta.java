package devs.mrp.gullproject.domains;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import devs.mrp.gullproject.service.LineaOperations;
import lombok.extern.slf4j.Slf4j;

public interface Propuesta {

	public String getId();
	public void setId(String id);
	
	public String getNombre();
	public void setNombre(String nombre);
	
	public boolean isRoot(); // si es o no consulta "original" (del cliente) o falso si es otro caso
	public boolean isForBid(); // verdadero si es "nuestra oferta" para enviar al cliente
	
	public void setLineaIds(List<String> ids);
	public void addLineaId(String lineaId);
	public void addLineaIds(List<String> lineaIds);
	public int getCantidadLineaIds();
	public boolean removeLineaId(String lineaId);
	public void removeLineaIds(List<String> lineaIds);
	public List<String> getAllLineaIds();
	public String getLineaIdByIndex(int index);
	public boolean updateLineaId(String idOriginal, String idDeseado);
	public boolean updateLineaIdByIndex(int index, String lineaId);
	public void addAttribute(AtributoForCampo att);
	public void removeAttribute(AtributoForCampo att);
	public void removeAttributeById(String id);
	public void setAttributeColumns(List<AtributoForCampo> attributes);
	public List<AtributoForCampo> getAttributeColumns();
	//public boolean saveOrder(Map<String, Integer> idlineaVSposicion); // Integer = posición-de-la-línea, String = id-de-linea
	
	public static boolean confirmaIguales(Linea linea1, Linea linea2) {
		LineaOperations operations1 = new LineaOperations(linea1);
		LineaOperations operations2 = new LineaOperations(linea2);
		if (operations1.getCantidadCampos() != operations2.getCantidadCampos()) {
			return false;
		}
		
		Map<String, Campo<?>> map1 = linea1.getCampos().stream().collect(Collectors.toMap((c)->c.getAtributoId(), (c)->c));
		Map<String, Campo<?>> map2 = linea2.getCampos().stream().collect(Collectors.toMap((c)->c.getAtributoId(), (c)->c));
		
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
			if (!map1.get(s).getDatos().equals(map2.get(s).getDatos())) {
				return false;
			}
		}
		
		return true;
	}
	public static boolean confirmaIgualesSegunCampos(Linea linea1, Linea linea2, List<AtributoForCampo> atributos) {
				Map<String, Campo<?>> map1 = linea1.getCampos().stream().collect(Collectors.toMap((c)->c.getAtributoId(), (c)->c));
				Map<String, Campo<?>> map2 = linea2.getCampos().stream().collect(Collectors.toMap((c)->c.getAtributoId(), (c)->c));
				
				Iterator<AtributoForCampo> it = atributos.iterator();
				while (it.hasNext()) {
					AtributoForCampo a = it.next();
					Campo<?> b = map1.get(a.getId());
					Campo<?> c = map2.get(a.getId());
					if(!b.getClass().equals(c.getClass())
							|| !b.getDatos().equals(c.getDatos())) {
						return false;
					}
				}
				
				return true;
	}
	public static boolean assertMatchesCampos(Linea l, Map<String, ?> atributoidVSvalue) {
		/**
		 * When values to compare don't exist, it takes the default (empy, zero, false...)
		 */
		
		Map<String, Campo<?>> mapLinea = l.getCampos().stream().collect(Collectors.toMap((c)->c.getAtributoId(), (c)->c));

		Set<String> atributoIds = atributoidVSvalue.keySet();

		Iterator<String> iterator = atributoIds.iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();

			if(mapLinea.containsKey(key) && (!mapLinea.get(key).getDatos().equals(atributoidVSvalue.get(key)))) {
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
		/**
		 * When a field to compare doesn't exist, directly returns false
		 */
		
		
		Map<String, Campo<?>> mapLinea = l.getCampos().stream().collect(Collectors.toMap((c)->c.getAtributoId(), (c)->c));

		Set<String> atributoIds = atributoidVSvalue.keySet();
		Set<String> lineaKeys = mapLinea.keySet();

		if (!lineaKeys.containsAll(atributoIds)) {
			return false;
		}
		
		Iterator<String> iterator = atributoIds.iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			
			if (!mapLinea.containsKey(key)
					|| !atributoidVSvalue.get(key).getClass().equals(mapLinea.get(key).getDatos().getClass())
					|| !atributoidVSvalue.get(key).equals(mapLinea.get(key).getDatos())) {
				return false;
			}
		}

		return true;
	}

}
