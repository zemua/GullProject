package devs.mrp.gullproject.service.linea.oferta.selectable;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;

@Service
public class SelectableLineMapperByCounterAndPvpFactoryImpl implements SelectableLineMapperByCounterAndPvpFactory {

	@Autowired SelectableAbstractLineFinderByCounterFactory factory;
	
	@Override
	public SelectableLineMapperByCounterAndPvp from(List<SelectableAbstractLine> lineas) {
		return new SelectableLineMapperByCounterAndPvpImpl(lineas, factory);
	}

}
