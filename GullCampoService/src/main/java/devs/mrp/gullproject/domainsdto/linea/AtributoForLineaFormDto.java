package devs.mrp.gullproject.domainsdto.linea;

import org.bson.types.ObjectId;

import devs.mrp.gullproject.validator.ValueMatchesTipoConstraint;
import lombok.Data;

@Data
public class AtributoForLineaFormDto {

	String localIdentifier = new ObjectId().toString(); // this is the local id to separate several attributes that have the same id in remote
	String id; // this is the id of the Atribute in the AtributeService, leaving this name for auto-mapping
	String name;
	String tipo;
	String value;
	
}
