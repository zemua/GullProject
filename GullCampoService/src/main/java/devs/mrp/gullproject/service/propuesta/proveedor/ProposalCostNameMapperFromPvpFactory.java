package devs.mrp.gullproject.service.propuesta.proveedor;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.IdName;
import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;

public interface ProposalCostNameMapperFromPvpFactory {

	public MyFinder<List<IdName>, String> from(PropuestaNuestra oferta, List<PropuestaProveedor> propuestas);
	
}
