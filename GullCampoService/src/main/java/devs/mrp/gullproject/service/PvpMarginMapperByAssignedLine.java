package devs.mrp.gullproject.service;

import devs.mrp.gullproject.ainterfaces.ListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MapperByDupla;
import devs.mrp.gullproject.domains.Linea;

public class PvpMarginMapperByAssignedLine implements MapperByDupla<Double, String, String> {

	ListOfAsignables<Linea> list;
	
	public PvpMarginMapperByAssignedLine(ListOfAsignables<Linea> list) {
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
