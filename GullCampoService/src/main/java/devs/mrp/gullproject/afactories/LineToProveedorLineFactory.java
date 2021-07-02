package devs.mrp.gullproject.afactories;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.LineaProveedor;

@Service
public class LineToProveedorLineFactory implements MyFactoryFromTo<Linea, LineaProveedor> {

	@Override
	public LineaProveedor from(Linea linea) {
		if (linea instanceof LineaProveedor) {
			return (LineaProveedor) linea;
		}
		var l = new LineaProveedor(linea);
		l.setId(linea.getId());
		return l;
	}
	
}
