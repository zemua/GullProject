package devs.mrp.gullproject.service.propuesta;

import java.util.List;

import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.Propuesta;

public interface AttributesExtractor {

	public List<AtributoForCampo> fromProposals(List<Propuesta> propuestas);
	
}
