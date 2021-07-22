package devs.mrp.gullproject.service.propuesta;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyListMerger;
import devs.mrp.gullproject.domains.propuestas.Propuesta;

@Service
public class ProposalIdsMergerFactory <T extends Propuesta> implements MyFactoryFromTo<List<T>, MyListMerger<String>> {

	@Override
	public MyListMerger<String> from(List<T> element) {
		return new ProposalIdsMerger<>(element);
	}

}
