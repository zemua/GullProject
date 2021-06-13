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
	final TipoPropuesta tipoPropuesta;
	
	/**
	 * parentId refers to the "previous version" of this proposal
	 */
	String parentId;
	
	/**
	 * forProposalId refers to the id of the proposal this is "replying" in case there is any
	 */
	String forProposalId;
	
	List<String> lineaIds = new ArrayList<>();
	List<AtributoForCampo> attributeColumns = new ArrayList<>();
	
	public Propuesta(TipoPropuesta tipo) {
		this.tipoPropuesta = tipo;
	}
	
	public PropuestaOperations operations() {
		return new PropuestaOperations(this);
	}
	
}
