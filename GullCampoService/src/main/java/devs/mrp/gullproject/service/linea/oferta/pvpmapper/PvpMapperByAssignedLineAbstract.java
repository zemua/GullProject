package devs.mrp.gullproject.service.linea.oferta.pvpmapper;

import devs.mrp.gullproject.domains.linea.PvperLinea;

public interface PvpMapperByAssignedLineAbstract {

	public PvperLinea findBy(String counterLineId, String pvpId);
	
}
