package devs.mrp.gullproject.service;

import devs.mrp.gullproject.ainterfaces.ListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MapperByDupla;
import devs.mrp.gullproject.domains.Linea;

public class PvpMapperByAssignedLine implements MapperByDupla<Double, String, String> {

	ListOfAsignables<Linea> list;
	
	public PvpMapperByAssignedLine(ListOfAsignables<Linea> list) { // TODO test
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
