package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import lombok.Data;

@Data
public class PropuestaProveedorOperations {

	private final PropuestaProveedor propuesta;
	
	public PropuestaProveedorOperations(PropuestaProveedor prop) {
		this.propuesta = prop;
	}
	
	public void initializeStandardCosts() {
		CosteProveedor coste = new CosteProveedor();
		coste.setName("COSTE BASE");
		List<CosteProveedor> list = new ArrayList<>();
		list.add(coste);
		propuesta.setCostes(list);
	}
	
	public boolean ifIsCosteProveedorId(String id) {
		Optional<CosteProveedor> cos = propuesta.getCostes().stream().filter(c -> c.getId().equals(id)).findAny();
		return cos.isPresent();
	}
	
}
