package devs.mrp.gullproject.domains.propuestas;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class Pvper {

	String id = new ObjectId().toString();
	List<String> idCostes;
	Map<String, List<String>> idAttributesByCost;
	@NotBlank
	String name;
	
}
