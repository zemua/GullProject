package devs.mrp.gullproject.service.linea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.service.facade.SupplierLineFinderByProposalAssignation;
import reactor.core.publisher.Mono;

@Service
public class SupplierLineByAssignationRetrieverFactory implements MyFactoryFromTo<String, MyListOfAsignables<Linea>> {

	@Autowired SupplierLineFinderByProposalAssignation finder;
	@Autowired LineByAssignationRetrieverFactory<Linea> factory;
	
	@Override
	public MyListOfAsignables<Linea> from(String element) {
		return new SupplierLineByAssignationRetriever(finder, factory, element);
	}

}
