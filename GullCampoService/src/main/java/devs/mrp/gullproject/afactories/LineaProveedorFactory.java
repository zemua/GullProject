package devs.mrp.gullproject.afactories;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryNew;
import devs.mrp.gullproject.domains.LineaProveedor;

@Service
public class LineaProveedorFactory implements MyFactoryNew<LineaProveedor> {

	@Override
	public LineaProveedor create() {
		return new LineaProveedor();
	}

}
