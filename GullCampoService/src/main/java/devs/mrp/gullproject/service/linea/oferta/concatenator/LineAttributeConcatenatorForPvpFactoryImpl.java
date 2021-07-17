package devs.mrp.gullproject.service.linea.oferta.concatenator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.service.linea.proveedor.LinesMapperByCounterIdFactory;
import devs.mrp.gullproject.service.propuesta.oferta.IncludeCotizacionCheckerFactory;

@Service
public class LineAttributeConcatenatorForPvpFactoryImpl implements LineAttributeConcatenatorForPvpFactory { // TODO test

	@Autowired LinesMapperByCounterIdFactory mapperFactory;
	@Autowired IncludeCotizacionCheckerFactory checkerFactory;
	
	@Override
	public LineAttributeConcatenatorForPvp from(List<Linea> lineasProveedor, List<Pvper> pvps, Consulta consulta) {
		return new LineAttributeConcatenatorForPvpImpl(mapperFactory.from(lineasProveedor), pvps, checkerFactory, consulta);
	}

}
