package devs.mrp.gullproject.service.linea.oferta;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFrom2To;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.service.linea.LineByAssignationRetrieverFactory;

public class PvpSumByAssignedLineIdFinderFactory implements PvpSumByCounterIdFactory {

	@Autowired LineByAssignationRetrieverFactory<Linea> retrieverFactory;
	@Autowired PvpSumForLineFinderFactory sumByLineFactory;
	
	@Override
	public MyMapperByDupla<Double, String, String> from(PropuestaNuestra propuestaNuestra, List<Linea> lineas) {
		return new PvpSumByAssignedLineIdFinder(lineas, propuestaNuestra, retrieverFactory, sumByLineFactory);
	}

}
