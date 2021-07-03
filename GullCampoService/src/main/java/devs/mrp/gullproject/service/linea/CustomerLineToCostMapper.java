package devs.mrp.gullproject.service.linea;

import org.springframework.beans.factory.annotation.Autowired;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.ainterfaces.MyParameterizedById;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.service.linea.proveedor.LineCostRetriever;

public class CustomerLineToCostMapper implements MyMapperByDupla<Double, String, String> {

	MyListOfAsignables<Linea> list;
	
	public CustomerLineToCostMapper(MyListOfAsignables<Linea> list) {
		this.list = list;
	}
	
	@Override
	public Double getByDupla(String lineIdAssignedTo, String proposalCostId) {
		Linea l = list.getAssignedTo(lineIdAssignedTo);
		MyParameterizedById<Double> costRetriever = new LineCostRetriever(l);
		return costRetriever.getParameter(proposalCostId);
	}
	
}
