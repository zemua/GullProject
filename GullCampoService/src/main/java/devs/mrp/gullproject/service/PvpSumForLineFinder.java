package devs.mrp.gullproject.service;

import java.util.concurrent.atomic.DoubleAdder;

import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaNuestra;
import lombok.Data;

@Data
public class PvpSumForLineFinder implements MyMapperByDupla<Double, Linea, String> {

	PropuestaNuestra propuesta;
	PropuestaNuestraOperations pops;
	
	public PvpSumForLineFinder(PropuestaNuestra propuesta) {
		this.propuesta = propuesta;
		pops = propuesta.operationsNuestra();
	}
	
	@Override
	public Double getByDupla(Linea linea, String pvpSumId) {
		LineaOperations lops = linea.operations();
		DoubleAdder result = new DoubleAdder();
		
		var sum = pops.getSumById(pvpSumId);
		sum.getPvperIds().stream().forEach(pvpId -> {
			if (linea.getPvps() != null && lops.getPvp(pvpId) != null) {
				result.add(lops.getPvp(pvpId).getPvp());
			}
		});
		
		return result.doubleValue();
	}

	
	
}
