package devs.mrp.gullproject.service.linea.oferta.pvpmapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.service.linea.AbstLineFinderFactory;

@Service
public class SumMapperByAssignedLineAbstractFactoryImpl implements SumMapperByAssignedLineAbstractFactory {

	@Autowired AbstLineFinderFactory factory;
	
	@Override
	public SumMapperByAssignedLineAbstract from(PropuestaNuestra propuestaNuestra, List<LineaAbstracta> lineas) {
		return new SumMapperByAssignedLineAbstractImpl(propuestaNuestra, factory.from(lineas));
	}

}
