package devs.mrp.gullproject.service.linea.oferta.selectable;

import java.util.List;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLinesWrap;
import devs.mrp.gullproject.service.linea.proveedor.LineAttributeConcatenator;

public interface LineReConstructor {

	public List<LineaAbstracta> from(SelectableLinesWrap wrap, LineAttributeConcatenator concatenator, List<AtributoForCampo> atributos, String forPropuestaId);
	
}
