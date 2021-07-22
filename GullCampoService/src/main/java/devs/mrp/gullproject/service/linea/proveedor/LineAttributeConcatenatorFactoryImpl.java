package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.Linea;

@Service
public class LineAttributeConcatenatorFactoryImpl implements LineAttributeConcatenatorFactory {

	@Autowired LinesMapperByCounterIdFactory mapperFactory;

	@Override
	public LineAttributeConcatenator from(List<Linea> lineas) {
		return new LineAttributeConcatenatorImpl(mapperFactory.from(lineas));
	}
	
}
