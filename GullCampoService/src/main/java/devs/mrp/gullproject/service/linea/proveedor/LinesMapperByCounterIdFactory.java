package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import devs.mrp.gullproject.domains.linea.Linea;

public interface LinesMapperByCounterIdFactory {

	public LinesMapperByCounterId from(List<Linea> lineas);
	
}
