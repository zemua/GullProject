package devs.mrp.gullproject.service.linea.proveedor;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryNew;
import devs.mrp.gullproject.domains.linea.LineaProveedor;

@Service
public class LineaProveedorFactory implements MyFactoryNew<LineaProveedor> {

	@Override
	public LineaProveedor create() {
		return new LineaProveedor();
	}

}
