package devs.mrp.gullproject.service.linea.oferta.selectable;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;

@Service
public class SelectableAbstractLineFinderByCounterFactoryImpl implements SelectableAbstractLineFinderByCounterFactory { // TODO test

	@Override
	public SelectableAbstractLineFinderByCounter from(List<SelectableAbstractLine> lineas) {
		return new SelectableAbstractLineFinderByCounterImpl(lineas);
	}

}
