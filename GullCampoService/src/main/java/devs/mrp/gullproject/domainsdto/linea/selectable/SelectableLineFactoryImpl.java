package devs.mrp.gullproject.domainsdto.linea.selectable;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstractaFactory;

@Service
public class SelectableLineFactoryImpl implements SelectableLineFactory {

	@Override
	public SelectableAbstractLine create() {
		return new SelectableLineImpl();
	}

	@Override
	public SelectableAbstractLine from(LineaAbstracta linea) {
		return new SelectableLineImpl(linea);
	}

	@Override
	public List<SelectableAbstractLine> from(List<LineaAbstracta> lineas) {
		List<SelectableAbstractLine> list = new ArrayList<>();
		lineas.forEach(l -> {
			list.add(from(l));
		});
		return list;
	}

}
