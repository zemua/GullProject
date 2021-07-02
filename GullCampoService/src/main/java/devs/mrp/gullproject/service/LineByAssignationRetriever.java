package devs.mrp.gullproject.service;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.domains.Linea;

public class LineByAssignationRetriever <T extends Linea> implements MyListOfAsignables<T> {

	List<T> lineas;
	
	public LineByAssignationRetriever(List<T> lineas) {
		this.lineas = lineas;
	}
	
	@Override
	public T getAssignedTo(String id) {
		return lineas.stream().filter(l -> l.operations().ifAssignedTo(id)).findAny().orElse(null);
	}

}
