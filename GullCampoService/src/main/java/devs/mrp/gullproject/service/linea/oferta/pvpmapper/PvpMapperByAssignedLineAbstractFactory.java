package devs.mrp.gullproject.service.linea.oferta.pvpmapper;

import java.util.List;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;

public interface PvpMapperByAssignedLineAbstractFactory {
	
	public PvpMapperByAssignedLineAbstract from(List<LineaAbstracta> lineas); 
	
}
