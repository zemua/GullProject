package devs.mrp.gullproject.service.linea.oferta;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaOferta;

public class PvpMapperByAssignedLine <T extends LineaOferta> implements MyMapperByDupla<Double, String, String> {

	MyListOfAsignables<T> list;
	
	public PvpMapperByAssignedLine(MyListOfAsignables<T> list) {
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
