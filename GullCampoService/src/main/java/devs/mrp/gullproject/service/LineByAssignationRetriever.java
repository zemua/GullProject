package devs.mrp.gullproject.service;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.ListOfAsignables;
import devs.mrp.gullproject.domains.Linea;

public class LineByAssignationRetriever implements ListOfAsignables<Linea> {

	List<Linea> lineas;
	
	public LineByAssignationRetriever(List<Linea> lineas) {
		this.lineas = lineas;
	}
	
	@Override
	public Linea getAssignedTo(String id) {
		return lineas.stream().filter(l -> l.operations().ifAssignedTo(id)).findAny().orElse(null);
	}

}
