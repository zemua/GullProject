package devs.mrp.gullproject.domains.dto;

import org.bson.types.ObjectId;

import devs.mrp.gullproject.validator.ValueMatchesTipoConstraint;
import lombok.Data;

@Data
public class AtributoForLineaFormDto {

	String localIdentifier = new ObjectId().toString(); // this is the local id to separate several attributes that have the same id in remote
	String id; // this is the id of the Atribute in the AtributeService
	String name;
	String tipo;
	String value;
	Boolean correct = true;
	
}
