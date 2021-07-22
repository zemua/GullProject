package devs.mrp.gullproject.service.linea.oferta.pvpmapper;

import java.util.List;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;

public interface SumMapperByAssignedLineAbstractFactory {

	public SumMapperByAssignedLineAbstract from(PropuestaNuestra propuestaNuestra, List<LineaAbstracta> lineas);
	
}
