package devs.mrp.gullproject.service.linea.oferta.concatenator;

import java.util.List;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.Pvper;

public interface LineAttributeConcatenatorForPvpFactory {

	public LineAttributeConcatenatorForPvp from(List<Linea> lineasProveedor, List<Pvper> pvps, Consulta consulta);
	
}
