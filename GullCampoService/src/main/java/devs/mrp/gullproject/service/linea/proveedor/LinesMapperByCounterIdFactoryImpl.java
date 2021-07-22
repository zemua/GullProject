package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.Linea;

@Service
public class LinesMapperByCounterIdFactoryImpl implements LinesMapperByCounterIdFactory {

	@Override
	public LinesMapperByCounterId from(List<Linea> lineas) {
		return new LinesMapperByCounterIdImpl(lineas);
	}

}
