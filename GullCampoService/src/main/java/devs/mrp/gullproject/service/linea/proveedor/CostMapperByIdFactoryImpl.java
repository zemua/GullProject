package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;

@Service
public class CostMapperByIdFactoryImpl implements CostMapperByIdFactory {

	@Override
	public MyMapperByDupla<Double, String, String> from(List<Linea> lineas) {
		return new CostMapperById(lineas);
	}

}
