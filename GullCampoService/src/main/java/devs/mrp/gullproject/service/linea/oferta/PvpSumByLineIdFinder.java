package devs.mrp.gullproject.service.linea.oferta;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;

public class PvpSumByLineIdFinder implements MyMapperByDupla<Double, String, String> { // TODO test with factory

	private MyListOfAsignables<Linea> lineas;
	private MyMapperByDupla<Double, Linea, String> sumByLineFinder;
	
	public PvpSumByLineIdFinder(String ourOfferId, List<Linea> listOfLines, PropuestaNuestra propuesta, MyFactoryFromTo<List<Linea>, MyListOfAsignables<Linea>> retrieverFactory, MyFactoryFromTo<PropuestaNuestra, MyMapperByDupla<Double, Linea, String>> sumByLineFactory) {
		lineas =  retrieverFactory.from(listOfLines);
		sumByLineFinder = sumByLineFactory.from(propuesta);
	}
	
	@Override
	public Double getByDupla(String assignedToLineId, String pvpSumId) {
		return sumByLineFinder.getByDupla(lineas.getAssignedTo(assignedToLineId), pvpSumId);
	}

	
	
}
