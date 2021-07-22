package devs.mrp.gullproject.service.propuesta.oferta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.Pvper;

public class IncludeCotizacionCheckerImpl implements IncludeCotizacionChecker {

	private Map<String, List<String>> cotizacionesVsCosts;
	private Pvper pvper;
	
	public IncludeCotizacionCheckerImpl(Pvper pvper, Consulta consulta) {
		this.pvper = pvper;
		this.cotizacionesVsCosts = consulta.operations().getPropuestasProveedor().stream().collect(Collectors.toMap((p) -> p.getId(), (p) -> ((PropuestaProveedor)p).getCostes().stream().map(c -> c.getId()).collect(Collectors.toList())));
	}
	
	@Override
	public boolean ifIncludes(String cotizacionId) {
		var costesPropuesta = cotizacionesVsCosts.get(cotizacionId);
		var costesPvp = pvper.getIdCostes();
		return costesPvp.stream().filter(c -> costesPropuesta.contains(c)).findAny().isPresent();
	}

}
