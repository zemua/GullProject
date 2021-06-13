package devs.mrp.gullproject.domains;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.PersistenceConstructor;

public class PropuestaNuestra extends Propuesta {

	public PropuestaNuestra() {
		super(TipoPropuesta.NUESTRA);
	}
	
	@PersistenceConstructor
	public PropuestaNuestra(TipoPropuesta tipoPropuesta) { // dummy parameter
		super(TipoPropuesta.NUESTRA);
	}
	
	public PropuestaNuestra(String customerProposalId) {
		super(TipoPropuesta.NUESTRA);
		this.setForProposalId(customerProposalId);
	}

}
