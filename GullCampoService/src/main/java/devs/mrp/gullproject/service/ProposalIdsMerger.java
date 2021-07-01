package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.gullproject.ainterfaces.ListMerger;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProposalIdsMerger implements ListMerger<String> {

	List<Propuesta> propuestas;
	
	public ProposalIdsMerger(List<Propuesta> propuestas) {
		this.propuestas = propuestas;
	}
	
	@Override
	public List<String> merge() {
		List<String> ids = new ArrayList<>();
		propuestas.stream().forEach(p -> {
			log.debug("adding proposal id to merge: " + p.getId() + " of " + p.toString());
			ids.add(p.getId());
		});
		return ids;
	}

}
