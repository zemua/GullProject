package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;

public interface CostMapperByIdFactory {

	public MyMapperByDupla<Double, String, String> from(List<Linea> lineas);
	
}
