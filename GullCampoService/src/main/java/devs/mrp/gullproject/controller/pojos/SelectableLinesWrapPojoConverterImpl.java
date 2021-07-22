package devs.mrp.gullproject.controller.pojos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLinesWrap;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLinesWrapFactory;

@Service
public class SelectableLinesWrapPojoConverterImpl implements SelectableLinesWrapPojoConverter {

	@Autowired SelectableLinesWrapFactory wrapFactory;
	
	@Override
	public SelectableLinesWrap fromPojo(SelectableLinesWrapPojo pojo) {
		var wrap = wrapFactory.create();
		wrap.getLineas().addAll(pojo.getLineas());
		return wrap;
	}

}
