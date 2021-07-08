package devs.mrp.gullproject.domainsdto.linea.selectable;

import org.springframework.stereotype.Service;

@Service
public class SelectableLineFactoryImpl implements SelectableLineFactory {

	@Override
	public SelectableAbstractLine create() {
		return new SelectableLineImpl();
	}

}
