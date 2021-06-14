package devs.mrp.gullproject.domains;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CosteLineaProveedor {

	@NotBlank
	String costeProveedorId;
	
	Double value;
	
}
