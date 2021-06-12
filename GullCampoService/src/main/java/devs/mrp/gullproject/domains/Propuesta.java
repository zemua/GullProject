package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import devs.mrp.gullproject.service.PropuestaOperations;
import lombok.Data;

@Data
public abstract class Propuesta {
	
	@Id
	String id = new ObjectId().toString();
	
	@NotBlank(message = "Selecciona un nombre")
	String nombre;
	
	@NotBlank
	TipoPropuesta tipoPropuesta;
	
	/**
	 * parentId refers to the "previous version" of this proposal
	 */
	String parentId;
	
	List<String> lineaIds = new ArrayList<>();
	List<AtributoForCampo> attributeColumns = new ArrayList<>();
	
	public PropuestaOperations operations() {
		return new PropuestaOperations(this);
	}
	
}
