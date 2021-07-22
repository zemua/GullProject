package devs.mrp.gullproject.domains.propuestas;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class AtributoForCampo {

	@NotBlank
	String localIdentifier = new ObjectId().toString(); // this is the local id to separate several attributes that have the same id in remote
	@NotBlank
	String id; // this is the id of the Atribute in the AtributeService, we keep the name for auto-mapping
	String name;
	String tipo;
	
	int order;
	
}
