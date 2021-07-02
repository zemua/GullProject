package devs.mrp.gullproject.domains.linea;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
