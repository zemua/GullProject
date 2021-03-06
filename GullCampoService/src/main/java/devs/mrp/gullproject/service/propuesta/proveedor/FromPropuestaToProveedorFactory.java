package devs.mrp.gullproject.service.propuesta.proveedor;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;

@Service
public class FromPropuestaToProveedorFactory implements MyFactoryFromTo<Propuesta, PropuestaProveedor> {

	@Override
	public PropuestaProveedor from(Propuesta element) {
		return new PropuestaProveedor(element);
	}

}
