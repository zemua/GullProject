package devs.mrp.gullproject.domainsdto.propuesta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import lombok.Data;

@Data
public class ProposalPie {

	PropuestaCliente propuestaCliente;
	List<PropuestaProveedor> propuestasProveedores = new ArrayList<>();
	Map<String, Integer> assignedLinesOfProp = new HashMap<>();
	List<PropuestaNuestra> propuestasNuestras = new ArrayList<>();
	
}
