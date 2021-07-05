package devs.mrp.gullproject.service.linea.oferta;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.service.linea.LineByAssignationRetrieverFactory;

@Service
public class PvpMapperByAssignedLineFactory <T extends Linea >implements MyFactoryFromTo<List<T>, MyMapperByDupla<Double, String, String>> {

	MyMapperByDupla<Double, String, String> mapper;
	@Autowired LineByAssignationRetrieverFactory<T> asignablesFactory;
	
	@Override
	public MyMapperByDupla<Double, String, String> from(List<T> lineas) {
		return new PvpMapperByAssignedLine<>(asignablesFactory.from(lineas));
	}

}
