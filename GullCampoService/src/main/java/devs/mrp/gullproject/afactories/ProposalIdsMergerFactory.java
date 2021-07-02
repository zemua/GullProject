package devs.mrp.gullproject.afactories;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyListMerger;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.service.ProposalIdsMerger;

public class ProposalIdsMergerFactory <T extends Propuesta> implements MyFactoryFromTo<List<T>, MyListMerger<String>> {

	@Override
	public MyListMerger<String> from(List<T> element) {
		return new ProposalIdsMerger<>(element);
	}

}
