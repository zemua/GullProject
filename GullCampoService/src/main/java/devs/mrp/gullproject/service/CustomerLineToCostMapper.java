package devs.mrp.gullproject.service;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.ainterfaces.MyParameterizedById;
import devs.mrp.gullproject.domains.CosteLineaProveedor;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.LineaProveedor;

public class CustomerLineToCostMapper implements MyMapperByDupla<Double, String, String> {

	MyListOfAsignables<LineaProveedor> list;
	
	public CustomerLineToCostMapper(MyListOfAsignables<LineaProveedor> list) {
		this.list = list;
	}
	
	@Override
	public Double getByDupla(String lineIdAssignedTo, String proposalCostId) {
		LineaProveedor l = list.getAssignedTo(lineIdAssignedTo);
		MyParameterizedById<Double> costRetriever = new LineCostRetriever(l);
		return costRetriever.getParameter(proposalCostId);
	}
	
}
