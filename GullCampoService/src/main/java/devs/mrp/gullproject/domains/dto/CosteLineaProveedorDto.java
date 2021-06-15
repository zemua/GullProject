package devs.mrp.gullproject.domains.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CosteLineaProveedorDto {

	@NotBlank
	String costeProveedorId;
	String name;
	Double value;
	
}
