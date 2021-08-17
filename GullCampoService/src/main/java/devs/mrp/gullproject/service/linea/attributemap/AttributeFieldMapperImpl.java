package devs.mrp.gullproject.service.linea.attributemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttributeFieldMapperImpl implements AttributeFieldMapper {

	private Map<String, List<LineaAbstracta>> lineasByCounter;
	
	public AttributeFieldMapperImpl(List<LineaAbstracta> lineasOferta) {
		initMap(lineasOferta);
	}
	
	@Override
	public String of(String lineaClienteId, String pvpOfertaId, String attributeOfertaId) {
		var counter = lineasByCounter.get(lineaClienteId);
		if (counter == null) {
			return "";
		}
		var linea = counter.stream().filter(l -> l.getPvp().getPvperId().equals(pvpOfertaId)).findAny();
		if (linea.isEmpty()) {
			log.debug("linea is empty");
			return "";
		}
		var att = linea.get().getCampos().stream().filter(c -> c.getAtributoId().equals(attributeOfertaId)).findAny();
		if (att.isEmpty()) {
			log.debug("att is empty");
			return "";
		}
		return att.get().getDatosText();
	}
	
	private void initMap(List<LineaAbstracta> lineasOferta) {
		lineasByCounter = new HashMap<>();
		lineasOferta.forEach(l -> {
			if (!lineasByCounter.containsKey(l.getCounterLineId())) {
				lineasByCounter.put(l.getCounterLineId(), new ArrayList<>());
			}
			lineasByCounter.get(l.getCounterLineId()).add(l);
		});
	}

}
