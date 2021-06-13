package devs.mrp.gullproject.domains;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PropuestaProveedor extends Propuesta {

	public PropuestaProveedor() {
		super(TipoPropuesta.PROVEEDOR);
	}
	
	public PropuestaProveedor(String idPropuestaCliente) {
		super(TipoPropuesta.PROVEEDOR);
		this.setForProposalId(idPropuestaCliente);
	}

}
