package devs.mrp.gullproject.service.propuesta.proveedor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFilter;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.TipoPropuesta;

@Service
public class PropuestaProveedorExtractorNoFlux implements MyFilter<List<Propuesta>, List<PropuestaProveedor>> {

	@Autowired  FromPropuestaToProveedorFactory factory;
	
	@Override
	public List<PropuestaProveedor> filter(List<Propuesta> input) {
		return input.stream().filter(p -> p.getTipoPropuesta().equals(TipoPropuesta.PROVEEDOR)).map(p -> factory.from(p)).collect(Collectors.toList());
	}

}
