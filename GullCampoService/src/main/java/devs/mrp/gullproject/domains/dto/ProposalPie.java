package devs.mrp.gullproject.domains.dto;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import lombok.Data;

@Data
public class ProposalPie {

	PropuestaCliente propuestaCliente;
	List<PropuestaProveedor> propuestasProveedores = new ArrayList<>();
	List<PropuestaNuestra> propuestasNuestras = new ArrayList<>();
	
}
