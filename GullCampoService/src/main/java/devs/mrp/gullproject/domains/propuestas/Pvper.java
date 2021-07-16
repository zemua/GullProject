package devs.mrp.gullproject.domains.propuestas;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Pvper {

	String id = new ObjectId().toString();
	List<String> idCostes;
	List<IdAttsList> idAttributesByCost;
	@NotBlank
	String name;
	
	@Data
	@NoArgsConstructor
	public static class IdAttsList {
		String costId;
		List<String> ids;
	}
	
}
