package devs.mrp.gullproject.domainsdto.propuesta;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import lombok.Data;

@Data
public class ProposalPie {

	PropuestaCliente propuestaCliente;
	List<PropuestaProveedor> propuestasProveedores = new ArrayList<>();
	List<PropuestaNuestra> propuestasNuestras = new ArrayList<>();
	
}
