package devs.mrp.gullproject.service;

import devs.mrp.gullproject.ainterfaces.ListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MapperByDupla;
import devs.mrp.gullproject.ainterfaces.ParametrizableById;
import devs.mrp.gullproject.domains.CosteLineaProveedor;
import devs.mrp.gullproject.domains.Linea;

public class CustomerLineToCostMapper implements MapperByDupla<Double, String, String> {

	ListOfAsignables<Linea> list;
	
	public CustomerLineToCostMapper(ListOfAsignables<Linea> list) {
		this.list = list;
	}
	
	@Override
	public Double getByDupla(String lineIdAssignedTo, String proposalCostId) {
		Linea l = list.getAssignedTo(lineIdAssignedTo);
		ParametrizableById<Double> costRetriever = new LineCostRetriever(l);
		return costRetriever.getParameter(proposalCostId);
	}
	
}
