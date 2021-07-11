package devs.mrp.gullproject.service.linea.oferta.pvpmapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.service.linea.AbstLineFinderFactory;

@Service
public class PvpMapperByAssignedLineAbstractFactoryImpl implements PvpMapperByAssignedLineAbstractFactory {

	@Autowired AbstLineFinderFactory factory;
	
	@Override
	public PvpMapperByAssignedLineAbstract from(List<LineaAbstracta> lineas) {
		return new PvpMapperByAssignedLineAbstractImpl(factory.from(lineas));
	}

}
