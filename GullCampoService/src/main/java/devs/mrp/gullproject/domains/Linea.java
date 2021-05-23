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
public class Linea {

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
		campos.addAll(campos);
	}
	
	@JsonIgnore
	public int getCantidadCampos() {
		return campos.size();
	}
	
	public Campo<?> getCampoByIndex(int i){
		return campos.get(i);
	}
	
	public boolean replaceCampo(String atributoId, Campo<?> c) {
		Iterator<Campo<?>> it = campos.iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(atributoId)) {
				campo = c;
				return true;
			}
		}
		return false;
	}
	
	public void addCampo(Campo<?> c) {
		campos.add(c);
	}
	
	public void removeCampo(String atributoId) {
		Iterator<Campo<?>> it = campos.iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(atributoId)) {
				it.remove();
			}
		}
	}
	
}
