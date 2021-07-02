package devs.mrp.gullproject.afactories;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.service.LineByAssignationRetriever;

@Service
public class LineByAssignationRetrieverFactory <T extends Linea> implements MyFactoryFromTo<List<T>, MyListOfAsignables<T>> {

	@Override
	public MyListOfAsignables<T> from(List<T> lineas) {
		return new LineByAssignationRetriever<>(lineas);
	}

}
