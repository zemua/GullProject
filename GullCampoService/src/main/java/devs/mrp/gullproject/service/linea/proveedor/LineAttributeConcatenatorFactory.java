package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import devs.mrp.gullproject.domains.linea.Linea;

public interface LineAttributeConcatenatorFactory {

	public LineAttributeConcatenator from(List<Linea> lineas);
	
}
