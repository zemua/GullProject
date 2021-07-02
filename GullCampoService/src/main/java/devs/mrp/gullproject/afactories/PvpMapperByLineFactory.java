package devs.mrp.gullproject.afactories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.LineaOferta;
import devs.mrp.gullproject.service.PvpMapperByAssignedLine;

@Service
public class PvpMapperByLineFactory <T extends LineaOferta> implements MyFactoryFromTo<List<T>, MyMapperByDupla<Double, String, String>> {

	MyMapperByDupla<Double, String, String> mapper;
	@Autowired LineByAssignationRetrieverFactory<T> asignablesFactory;
	
	@Override
	public MyMapperByDupla<Double, String, String> from(List<T> lineas) {
		return new PvpMapperByAssignedLine<>(asignablesFactory.from(lineas));
	}

}
