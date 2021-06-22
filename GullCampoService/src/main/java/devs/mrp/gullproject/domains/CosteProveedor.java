package devs.mrp.gullproject.domains;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CosteProveedor {

	@NotBlank
	String id = new ObjectId().toString();
	@NotBlank(message = "El nombre no debe estar vacío")
	String name;
	
	public CosteProveedor(CosteProveedor coste) {
		this.id = coste.getId();
		this.name = coste.getName();
	}
	
}
