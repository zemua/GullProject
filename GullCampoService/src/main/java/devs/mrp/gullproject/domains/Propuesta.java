package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import devs.mrp.gullproject.service.PropuestaOperations;
import lombok.Data;

@Data
public abstract class Propuesta {
	
	@Id
	String id = new ObjectId().toString();
	
	@NotBlank(message = "Selecciona un nombre")
	String nombre;
	
	@NotNull
	private final TipoPropuesta tipoPropuesta;
	
	long createdTime; // TODO to set on creation of new ones only
	
	/**
	 * parentId refers to the "previous version" of this proposal
	 */
	String parentId;
	
	/**
	 * forProposalId refers to the id of the proposal this is "replying" in case there is any
	 * if it is a customer proposal, it refers to the consulta
	 */
	@NotBlank
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
