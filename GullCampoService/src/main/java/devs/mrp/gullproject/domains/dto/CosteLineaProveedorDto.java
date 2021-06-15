package devs.mrp.gullproject.domains.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CosteLineaProveedorDto {

	@NotBlank
	String id; // the id of the cost in the proposal
	String name;
	Double value;
	
}
