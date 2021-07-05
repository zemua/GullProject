package devs.mrp.gullproject.service.propuesta.proveedor;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.IdName;
import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;

public class ProposalCostNameMapperFromPvpFactoryImpl implements ProposalCostNameMapperFromPvpFactory {

	@Override
	public MyFinder<List<IdName>, String> from(PropuestaNuestra oferta, List<PropuestaProveedor> propuestas) {
		return new ProposalCostNameMapperFromPvp(oferta, propuestas);
	}

}
