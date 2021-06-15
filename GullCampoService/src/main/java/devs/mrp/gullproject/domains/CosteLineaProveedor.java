package devs.mrp.gullproject.domains;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CosteLineaProveedor {

	public CosteLineaProveedor(String costeId) {
		this.costeProveedorId = costeId;
		value = 0D;
	}
	
	public CosteLineaProveedor (CosteLineaProveedor coste) {
		this.costeProveedorId = coste.getCosteProveedorId();
		this.value = coste.getValue();
	}
	
	@NotBlank
	String costeProveedorId;
	
	double value;
	
}
