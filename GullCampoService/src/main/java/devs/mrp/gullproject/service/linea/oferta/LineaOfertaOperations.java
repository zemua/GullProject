package devs.mrp.gullproject.service.linea.oferta;

import java.util.Iterator;
import java.util.Optional;

import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.LineaOferta;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.service.linea.LineaOperations;

public class LineaOfertaOperations extends LineaOperations {

	LineaOferta linea;
	
	public LineaOfertaOperations(LineaOferta linea) {
		super(linea);
		this.linea = linea;
	}
	
	public boolean ifHasPvp(String pvpId) {
		Optional<PvperLinea> pvp = linea.getPvps().stream().filter(p -> p.getPvperId().equals(pvpId)).findAny();
		return pvp.isPresent();
	}
	
	public PvperLinea getPvp(String pvpId) {
		return linea.getPvps().stream().filter(p -> p.getPvperId().equals(pvpId)).findAny().orElse(null);
	}
	
	public Double getPvpValue(String pvpId) {
		var pvp = getPvp(pvpId);
		if (pvp == null) {
			return 0D;
		}
		return pvp.getPvp();
	}
	
	public Double getPvpMargin(String pvpId) {
		var pvp = getPvp(pvpId);
		if (pvp == null) {
			return 0D;
		}
		return pvp.getMargen();
	}
	
	public void removePvpById(String pvpId) {
		Iterator<PvperLinea> it = linea.getPvps().iterator();
		while (it.hasNext()) {
			PvperLinea linea = it.next();
			if (linea.getPvperId().equals(pvpId)) {
				it.remove();
			}
		}
	}

}
