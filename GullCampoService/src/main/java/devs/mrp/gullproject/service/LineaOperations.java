package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
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
		return null;
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
	
}
