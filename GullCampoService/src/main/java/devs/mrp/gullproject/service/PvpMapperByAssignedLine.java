package devs.mrp.gullproject.service;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.Linea;

public class PvpMapperByAssignedLine implements MyMapperByDupla<Double, String, String> {

	MyListOfAsignables<Linea> list;
	
	public PvpMapperByAssignedLine(MyListOfAsignables<Linea> list) {
		this.list = list;
	}
	
	@Override
	public Double getByDupla(String lineId, String pvpId) {
		double d;
		try {
			d = list.getAssignedTo(lineId).operations().getPvpValue(pvpId);
		} catch (Exception e) {
			return 0D;
		}
		return d;
	}

}
