package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Document (collection = "lineas")
public class Linea { // TODO test methods

	@Id
	private String id = new ObjectId().toString();
	
	@NotBlank(message = "Selecciona un nombre")
	private String nombre;
	
	/**
	 * This is the id of the propuesta to which this one belongs
	 */
	@NotBlank
	private String propuestaId;
	
	/**
	 * When creating an updated snapshot, this will refer to the "old" line
	 */
	private String parentId;
	/**
	 * When creating an offer of suppliers or ours, this will refer to the customer line
	 */
	private String counterLineId;
	
	private Integer order;
	
	/**
	 * Then the proposal will have an ArrayList of attributes, that can have repeated ones, for example 2 dimensions for inlet and outlet
	 * Finally the line will have attribute fields, filled in a table, and controlled by javascript the type of data (and server side too)
	 * if the proposal has an attribute that the line doesn't, then it shows blank
	 * if the line has an attribute that the proposal doesn't, then it doesn't show
	 */
	private List<Campo<?>> campos = new ArrayList<>();
	
	public void resetCampos(List<Campo<Object>> campos) {
		this.campos.clear();
		this.campos.addAll(campos);
	}
	
	@JsonIgnore
	public int getCantidadCampos() {
		return campos.size();
	}
	
	public Map<String, Campo<?>> getMapOfCamposByAtributoId() {
		return this.campos.stream().collect(Collectors.toMap((c)->c.getAtributoId(), (c)->c));
	}
	
	public Campo<?> getCampoByIndex(int i){
		return campos.get(i);
	}
	
	public Campo<?> getCampoByAttId(String attId) {
		Iterator<Campo<?>> it = campos.iterator();
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
		Iterator<Campo<?>> it = this.campos.iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(atributoId)) {
				it.remove();
				removed = true;
				break;
			}
		}
		if (removed) { campos.add(c); }
		return removed;
	}
	
	public void replaceOrElseAddCampo(String atributoId, Campo<?> c) {
		if (!replaceCampo(atributoId, c)) {
			this.campos.add(c);
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
		campos.add(c);
	}
	
	public void removeCampoByAttId(String atributoId) {
		Iterator<Campo<?>> it = campos.iterator();
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
	
	public boolean equals(Linea linea) {
		if (!this.nombre.equals(linea.getNombre())) {return false;}
		if (!this.propuestaId.equals(linea.getPropuestaId())) {return false;}
		if (!this.parentId.equals(linea.getParentId())) {return false;}
		if (!this.counterLineId.equals(linea.getCounterLineId())) {return false;}
		if (!this.order.equals(linea.getOrder())) {return false;}
		if (this.campos.size() != linea.getCampos().size()) {return false;}
		for (int i = 0; i<campos.size(); i++) {
			Campo<?> c = campos.get(i);
			Campo<?> d = linea.getCampos().get(i);
			if (!c.getAtributoId().equals(d.getAtributoId())) {return false;}
			if (!c.getDatos().equals(d.getDatos())) {return false;}
			// don't compare individual ids of each campo
		}
		return true;
	}
	
}
