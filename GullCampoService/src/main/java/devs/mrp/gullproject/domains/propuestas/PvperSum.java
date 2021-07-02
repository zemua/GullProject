package devs.mrp.gullproject.domains.propuestas;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class PvperSum {

	String id = new ObjectId().toString();
	List<String> pvperIds;
	@NotBlank(message = "Debes seleccionar un nombre")
	String name;
	
}
