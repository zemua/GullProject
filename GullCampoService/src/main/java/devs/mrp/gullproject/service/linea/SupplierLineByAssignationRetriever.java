package devs.mrp.gullproject.service.linea;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.domains.linea.Linea;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SupplierLineByAssignationRetriever implements MyListOfAsignables<Linea> { // TODO test with factory

	MyFinder<List<Linea>, String> finder;
	MyFactoryFromTo<List<Linea>, MyListOfAsignables<Linea>> assignablesFactory;
	private MyListOfAsignables<Linea> lineas;
	
	@Autowired
	public SupplierLineByAssignationRetriever(MyFinder<List<Linea>, String> finder, MyFactoryFromTo<List<Linea>, MyListOfAsignables<Linea>> assignablesFactory, String propuestaId) {
		this.finder = finder;
		this.assignablesFactory = assignablesFactory;
		init(propuestaId);
	}
	
	@Override
	public Linea getAssignedTo(String counterLineId) {
		return lineas.getAssignedTo(counterLineId);
	}
	
	private void init(String customerProposalId) {
		lineas = assignablesFactory.from(finder.findBy(customerProposalId));
	}

}
