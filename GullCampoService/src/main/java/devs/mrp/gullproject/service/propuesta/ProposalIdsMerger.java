package devs.mrp.gullproject.service.propuesta;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyListMerger;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProposalIdsMerger <T extends Propuesta> implements MyListMerger<String> {

	List<T> propuestas;
	
	public ProposalIdsMerger(List<T> propuestas) {
		this.propuestas = propuestas;
	}
	
	@Override
	public List<String> merge() {
		List<String> ids = new ArrayList<>();
		propuestas.stream().forEach(p -> {
			ids.add(p.getId());
		});
		return ids;
	}

}
