package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.CosteLineaProveedor;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.PvperLinea;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class LineaOperations {

	private final Linea linea;
	
	public LineaOperations(Linea linea) {
		this.linea = linea;
	}
	
	public Linea clonar() {
		return new Linea(this.linea);
	}
	
	public void resetCampos(List<Campo<Object>> campos) {
		linea.getCampos().clear();
		linea.getCampos().addAll(campos);
	}
	
	@JsonIgnore
	public int getCantidadCampos() {
		return linea.getCampos().size();
	}
	
	public Map<String, Campo<?>> getMapOfCamposByAtributoId() {
		return linea.getCampos().stream().collect(Collectors.toMap((c)->c.getAtributoId(), (c)->c));
	}
	
	public Campo<?> getCampoByIndex(int i){
		return linea.getCampos().get(i);
	}
	
	public Campo<?> getCampoByAttId(String attId) {
		Iterator<Campo<?>> it = linea.getCampos().iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(attId)) {
				return campo;
			}
		}
		return new Campo<String>(attId, "");
	}
	
	public String getValueByAttId(String attId) {
		Campo<?> c = getCampoByAttId(attId);
		if (c == null || c.getDatos() == null) {
			return "";
		} else {
			return String.valueOf(c.getDatos());
		}
	}
	
	public boolean replaceCampo(String atributoId, Campo<?> c) {
		boolean removed = false;
		Iterator<Campo<?>> it = linea.getCampos().iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(atributoId)) {
				it.remove();
				removed = true;
				break;
			}
		}
		if (removed) { linea.getCampos().add(c); }
		return removed;
	}
	
	public boolean replaceCampo(Campo<?> c) {
		return replaceCampo(c.getAtributoId(), c);
	}
	
	public void replaceOrElseAddCampo(String atributoId, Campo<?> c) {
		if (!replaceCampo(atributoId, c)) {
			linea.getCampos().add(c);
		}
	}
	
	public void replaceOrElseAddCampos(List<Campo<?>> campos) {
		campos.stream().forEach(c -> replaceOrElseAddCampo(c.getAtributoId(), c));
	}
	
	public void replaceOrAddCamposObj(List<Campo<Object>> campos) {
		List<Campo<?>> cs = new ArrayList<>();
		cs.addAll(campos);
		replaceOrElseAddCampos(cs);
	}
	
	public void addCampo(Campo<?> c) {
		linea.getCampos().add(c);
	}
	
	public void removeCampoByAttId(String atributoId) {
		Iterator<Campo<?>> it = linea.getCampos().iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(atributoId)) {
				it.remove();
			}
		}
	}
	
	public void removeCamposByAttId(List<String> attIds) {
		attIds.stream().forEach(att -> removeCampoByAttId(att));
	}
	
	public CosteLineaProveedor getCosteByCosteId(String costeId) {
		if (linea.getCostesProveedor() == null) {
			return new CosteLineaProveedor(costeId);
		}
		Optional<CosteLineaProveedor> cos = linea.getCostesProveedor().stream().filter(c -> c.getCosteProveedorId().equals(costeId)).findFirst();
		return cos.orElse(new CosteLineaProveedor(costeId));
	}
	
	public boolean ifHasPvp(String pvpId) {
		Optional<PvperLinea> pvp = linea.getPvps().stream().filter(p -> p.getPvperId().equals(pvpId)).findAny();
		return pvp.isPresent();
	}
	
	public PvperLinea getPvp(String pvpId) {
		return linea.getPvps().stream().filter(p -> p.getPvperId().equals(pvpId)).findAny().orElse(null);
	}
	
	public Double getPvpValue(String pvpId) {
		var pvp = getPvp(pvpId);
		if (pvp == null) {
			return 0D;
		}
		return pvp.getPvp();
	}
	
	public Double getPvpMargin(String pvpId) {
		var pvp = getPvp(pvpId);
		if (pvp == null) {
			return 0D;
		}
		return pvp.getMargen();
	}
	
	public void removePvpById(String pvpId) {
		Iterator<PvperLinea> it = linea.getPvps().iterator();
		while (it.hasNext()) {
			PvperLinea linea = it.next();
			if (linea.getPvperId().equals(pvpId)) {
				it.remove();
			}
		}
	}
	
	public boolean ifHasCost(String costId) {
		Optional<CosteLineaProveedor> coste = linea.getCostesProveedor().stream().filter(c -> c.getCosteProveedorId().equals(costId)).findAny();
		return coste.isPresent();
	}
	
	public void removeCosteById(String costId) {
		Iterator<CosteLineaProveedor> it = linea.getCostesProveedor().iterator();
		while (it.hasNext()) {
			var cos = it.next();
			if (cos.getCosteProveedorId().equals(costId)) {
				it.remove();
			}
		}
	}
	
	public boolean ifAssignedTo(String id) {
		Optional<String> assigned = linea.getCounterLineId().stream().filter(c -> c.equals(id)).findAny();
		return assigned.isPresent();
	}
	
	/**
	 * 
	 */
	
	public boolean equals(Linea nLinea) {
		if (!checkEquality(linea.getNombre(), nLinea.getNombre())) {
			log.debug("line name not equal");
			return false;
			}
		if (!checkEquality(linea.getPropuestaId(), nLinea.getPropuestaId())) {
			log.debug("propuestaId not equal");
			return false;
			}
		if (!checkEquality(linea.getParentId(), nLinea.getParentId())) {
			log.debug("parentId not equal");
			return false;
			}
		if (!checkEquality(linea.getCounterLineId(), nLinea.getCounterLineId())) {
			log.debug("counterLineId not equal");
			return false;
			}
		if (linea.getCampos().size() != nLinea.getCampos().size()) {
			log.debug("campos.size not equal");
			return false;
			}
		for (int i = 0; i<linea.getCampos().size(); i++) {
			Campo<?> c = linea.getCampos().get(i);
			Campo<?> d = nLinea.getCampos().get(i);
			if (!checkEquality(c.getAtributoId(), d.getAtributoId())) {
				log.debug("atribute " + c.getAtributoId() + "not equal");
				return false;
				}
			if (!checkEquality(c.getDatos(), d.getDatos())) {
				log.debug("datos not equals for " + c.getDatosText() + " and " + d.getDatosText());
				return false;
				}
			// don't compare individual ids of each campo
		}
		return true;
	}
	
	private boolean checkEquality(Object a, Object b) {
		if (a == null && b != null) {
			return false;
		}
		if (a != null && b == null) {
			return false;
		}
		if (a == null && b == null) {
			return true;
		}
		if (a != null && b != null) {
			return a.equals(b);
		}
		return false;
	}
	
	/**
	 * STATIC METHODS
	 */
	
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
