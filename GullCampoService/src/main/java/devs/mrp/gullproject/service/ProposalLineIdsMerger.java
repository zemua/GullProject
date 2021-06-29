package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.gullproject.ainterfaces.ListMerger;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;

public class ProposalLineIdsMerger implements ListMerger<String> {

	List<Propuesta> propuestas;
	
	public ProposalLineIdsMerger(List<Propuesta> propuestas) {
		this.propuestas = propuestas;
	}
	
	@Override
	public List<String> merge() {
		List<String> lineas = new ArrayList<>();
		propuestas.stream().forEach(p -> {
			lineas.addAll(p.getLineaIds());
		});
		return null;
	}

}
