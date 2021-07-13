package devs.mrp.gullproject.service.linea.oferta.selectable;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstractaFactory;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLinesWrap;
import devs.mrp.gullproject.service.linea.proveedor.LineAttributeConcatenator;

@Service
public class LineReConstructorImpl implements LineReConstructor {
	
	@Override
	public List<LineaAbstracta> from(SelectableLinesWrap wrap, LineAttributeConcatenator concatenator, List<AtributoForCampo> atributos, String forPropuestaId) {
		List<LineaAbstracta> lineas = new ArrayList<>();
		wrap.getLineas().stream().filter(l -> l.getSelected()).forEach(lin -> {
			lin.setCampos(new ArrayList<>());
			lin.setPropuestaId(forPropuestaId);
			atributos.forEach(att -> {
				Campo<String> campo = new Campo<>();
				campo.setAtributoId(att.getId());
				campo.setDatos(concatenator.forLineAtt(lin.getCounterLineId(), att.getId()));
				lin.getCampos().add(campo);
			});
			lineas.add(lin);
		});
		return lineas;
	}

}
