package devs.mrp.gullproject.service.linea.oferta.pvpmapper;

import java.util.stream.Collectors;

import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.service.linea.AbstLineFinder;

public class PvpMapperByAssignedLineAbstractImpl implements PvpMapperByAssignedLineAbstract {

	private AbstLineFinder lineFinder;
	
	public PvpMapperByAssignedLineAbstractImpl(AbstLineFinder lineFinder) {
		this.lineFinder = lineFinder;
	}
	
	@Override
	public PvperLinea findBy(String counterLineId, String pvpId) {
		PvperLinea defecto = new PvperLinea();
		defecto.setMargen(0);
		defecto.setPvp(0);
		defecto.setPvperId(pvpId);
		
		var pvp = lineFinder.findBy(counterLineId).stream().filter(l -> l.getPvp().getPvperId().equals(pvpId)).findAny();
		if (pvp.isPresent()) {
			return pvp.get().getPvp();
		} else {
			return defecto;
		}
	}

}
