package devs.mrp.gullproject.domainsdto.propuesta;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class AtributoForFormDto {

	String localIdentifier = new ObjectId().toString(); // this is the local id to separate several attributes that have the same id in remote
	String id; // this is the id of the Atribute in the AtributeService
	String name;
	String tipo;
	Boolean selected;
	
}
