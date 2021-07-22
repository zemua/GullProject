package devs.mrp.gullproject.domainsdto.linea.selectable;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectableLinesWrapFactoryImpl implements SelectableLinesWrapFactory {

	@Autowired SelectableLineFactory factory;
	
	@Override
	public SelectableLinesWrap create() {
		var wrap = new SelectableLinesWrapImpl();
		wrap.setLineas(new ArrayList<>());
		return wrap;
	}

	@Override
	public SelectableLinesWrap withNumberOfLines(int n) {
		var wrap = create();
		for (int i=0; i<n; i++) {
			wrap.getLineas().add(factory.create());
		}
		return wrap;
	}

}
