package devs.mrp.gullproject.service.linea.attributemap;

import java.util.List;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;

public interface AttributeFieldMapperFactory {

	public AttributeFieldMapper from(List<LineaAbstracta> lineasOferta);
	
}
