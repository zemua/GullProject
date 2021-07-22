package devs.mrp.gullproject.service.linea.oferta;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;

public class PvpMarginMapperByAssignedLine implements MyMapperByDupla<Double, String, String> {

	MyListOfAsignables<Linea> list;
	
	public PvpMarginMapperByAssignedLine(MyListOfAsignables<Linea> list) {
		this.list = list;
	}
	
	@Override
	public Double getByDupla(String assignedToLineId, String pvpId) {
		if (list == null) {
			return 0D;
		}
		var assigned = list.getAssignedTo(assignedToLineId);
		if (assigned == null) {
			return 0D;
		}
		Double d = assigned.operations().getPvpMargin(pvpId);
		if(d == null) {
			return 0D;
		}
		return d;
	}

}
