package devs.mrp.gullproject.service;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.Linea;

public class PvpMarginMapperByAssignedLine implements MyMapperByDupla<Double, String, String> {

	MyListOfAsignables<Linea> list;
	
	public PvpMarginMapperByAssignedLine(MyListOfAsignables<Linea> list) {
		this.list = list;
	}
	
	@Override
	public Double getByDupla(String lineId, String pvpId) {
		if (list == null) {
			return 0D;
		}
		Double d = list.getAssignedTo(lineId).operations().getPvpMargin(pvpId);
		if(d == null) {
			return 0D;
		}
		return d;
	}

}
