package devs.mrp.gullproject.service.propuesta;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.Propuesta;

@Service
public class AtributesExtractorImpl implements AttributesExtractor {
	
	@Override
	public List<AtributoForCampo> fromProposals(List<Propuesta> propuestas) {
		List<AtributoForCampo> atributos = new ArrayList<>();
		propuestas.forEach(prop -> {
			prop.getAttributeColumns().forEach(att -> {
				if (!containsAtt(atributos, att)) {
					atributos.add(att);
				}
			});
		});
		return atributos;
	}
	
	private boolean containsAtt(List<AtributoForCampo> atributos, AtributoForCampo att) {
		return atributos.stream().filter(a -> a.getId().equals(att.getId())).findAny().isPresent();
	}

}
