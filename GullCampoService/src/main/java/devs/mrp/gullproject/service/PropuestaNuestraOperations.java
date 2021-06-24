package devs.mrp.gullproject.service;

import java.util.List;

import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.Pvper;
import lombok.Data;

@Data
public class PropuestaNuestraOperations extends PropuestaOperations {

	protected final PropuestaNuestra propuestaNuestra;
	
	public PropuestaNuestraOperations(PropuestaNuestra prop) {
		super(prop);
		propuestaNuestra = (PropuestaNuestra)propuesta;
	}
	
}
