package devs.mrp.gullproject.domains;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class CosteProveedor {

	@NotBlank
	String id = new ObjectId().toString();
	@NotBlank(message = "El nombre no debe estar vacío")
	String name;
	
}
