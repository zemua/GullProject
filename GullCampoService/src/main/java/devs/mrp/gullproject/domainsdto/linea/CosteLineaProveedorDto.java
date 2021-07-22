package devs.mrp.gullproject.domainsdto.linea;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CosteLineaProveedorDto {

	@NotBlank
	String id; // the id of the cost in the proposal
	String name;
	Double value;
	
}
