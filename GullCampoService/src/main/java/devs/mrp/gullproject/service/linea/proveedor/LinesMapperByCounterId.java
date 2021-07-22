package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import devs.mrp.gullproject.domains.linea.Linea;

public interface LinesMapperByCounterId {

	public List<Linea> forCounter(String counterId);
	
}
