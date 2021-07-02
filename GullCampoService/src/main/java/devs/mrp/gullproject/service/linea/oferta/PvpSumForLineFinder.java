package devs.mrp.gullproject.service.linea.oferta;

import java.util.concurrent.atomic.DoubleAdder;

import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaOferta;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.service.propuesta.oferta.PropuestaNuestraOperations;
import lombok.Data;

@Data
public class PvpSumForLineFinder implements MyMapperByDupla<Double, LineaOferta, String> {

	PropuestaNuestra propuesta;
	PropuestaNuestraOperations pops;
	
	public PvpSumForLineFinder(PropuestaNuestra propuesta) {
		this.propuesta = propuesta;
		pops = propuesta.operationsNuestra();
	}
	
	@Override
	public Double getByDupla(LineaOferta linea, String pvpSumId) {
		LineaOfertaOperations lops = linea.operations();
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
