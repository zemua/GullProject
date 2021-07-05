package devs.mrp.gullproject.service.linea;

import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.gullproject.ainterfaces.MyFilter;
import devs.mrp.gullproject.domains.linea.Linea;

public class LineByProposalFilter implements MyFilter<List<String>, List<Linea>> {

	private List<Linea> lineas;
	
	public LineByProposalFilter(List<Linea> lineas) {
		this.lineas = lineas;
	}
	
	@Override
	public List<Linea> filter(List<String> proposalIds) {
		return lineas.stream().filter(l -> proposalIds.contains(l.getPropuestaId())).collect(Collectors.toList());
	}

}
