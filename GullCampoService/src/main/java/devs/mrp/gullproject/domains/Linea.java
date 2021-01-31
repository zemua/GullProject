package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

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
	 * TODO
	 * First create an ArrayList of Atributes that are allowed to have as columns, inside the proposals
	 * Then the proposal will have an array of attributes, that can have repeated ones, for example 2 dimensions for in and out
	 * Finally the line will have as many fields as attributes in the proposal, filled in a table, and controlled by javascript the type of data (and server side too)
	 */
	private List<Campo<?>> campos = new ArrayList<>();
	
	@JsonIgnore
	public int getCantidadCampos() {
		return campos.size();
	}
	
	public Campo<?> getCampoByIndex(int i){
		return campos.get(i);
	}
	
	public void addCampo(Campo<?> c) {
		campos.add(c);
	}
	
}
