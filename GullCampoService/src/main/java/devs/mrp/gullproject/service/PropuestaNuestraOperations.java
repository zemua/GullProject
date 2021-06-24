package devs.mrp.gullproject.service;

import java.util.List;

import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.Pvper;
import lombok.Data;

@Data
public class PropuestaNuestraOperations {

	private final PropuestaNuestra propuesta;
	
	public PropuestaNuestraOperations(PropuestaNuestra propuesta) {
		this.propuesta = propuesta;
	}
	
}
