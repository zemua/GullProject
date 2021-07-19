package devs.mrp.gullproject.service.linea.attributemap;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;

@Service
public class AttributeFieldMapperFactoryImpl implements AttributeFieldMapperFactory {
	
	@Override
	public AttributeFieldMapper from(List<LineaAbstracta> lineasOferta) {
		return new AttributeFieldMapperImpl(lineasOferta);
	}

}
