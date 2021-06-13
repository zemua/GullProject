package devs.mrp.gullproject.domains;

import java.util.List;
import java.util.Map;

public class PropuestaNuestra extends Propuesta {

	public PropuestaNuestra() {
		super(TipoPropuesta.NUESTRA);
	}
	
	public PropuestaNuestra(String customerProposalId) {
		super(TipoPropuesta.NUESTRA);
		this.setForProposalId(customerProposalId);
	}

}
