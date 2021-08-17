package devs.mrp.gullproject.service.linea.oferta.concatenator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.service.linea.proveedor.LinesMapperByCounterId;
import devs.mrp.gullproject.service.propuesta.oferta.IncludeCotizacionChecker;
import devs.mrp.gullproject.service.propuesta.oferta.IncludeCotizacionCheckerFactory;

public class LineAttributeConcatenatorForPvpImpl implements LineAttributeConcatenatorForPvp {

	private LinesMapperByCounterId linesMapper;
	private Map<String, Pvper> pvperMapper;
	private IncludeCotizacionCheckerFactory checkerFact;
	private Consulta consulta;
	
	public LineAttributeConcatenatorForPvpImpl(LinesMapperByCounterId linesMapper, List<Pvper> pvps, IncludeCotizacionCheckerFactory checkerFact, Consulta consulta) {
		this.linesMapper = linesMapper;
		this.pvperMapper = pvps.stream().collect(Collectors.toMap((pvp) -> pvp.getId(), (pvp) -> pvp));
		this.checkerFact = checkerFact;
		this.consulta = consulta;
	}
	
	@Override
	public String forLineAttPvp(String counterLineId, String attId, String pvpId) {
		List<Linea> lineas = linesMapper.forCounter(counterLineId);
		
		Pvper pvp = pvperMapper.get(pvpId);
		
		StringBuilder builder = new StringBuilder();
		lineas.forEach(l -> {
			if (includedInPvp(pvp, l.getPropuestaId())) {
				if (attIncluded(pvp, l.getPropuestaId(), attId)) {
					var appender = getAtt(l, attId);
					if (!appender.isEmpty() && !builder.toString().isEmpty()) {
						builder.append(" / ");
					}
					builder.append(appender);
				}
			}
		});
		return builder.toString();
	}
	
	private boolean includedInPvp(Pvper pvper, String cotizacionId) {
		return checkerFact.from(pvper, consulta).ifIncludes(cotizacionId);
	}
	
	private boolean attIncluded(Pvper pvper, String cotizacionId, String attId) {
		var idAtt = pvper.getIdAttributesByCotiz();
		if (idAtt == null) {
			return false;
		}
		var atts = idAtt.stream().filter(a -> a.getCotizId().equals(cotizacionId)).findAny();
		if (atts.isEmpty()) { return false; }
		return atts.get().getIds().stream().filter(a -> a.equals(attId)).findAny().isPresent();
	}
	
	private String getAtt(Linea linea, String attId) {
		return linea.getCampos().stream().filter(c -> c.getAtributoId().equals(attId)).map(c -> c.getDatosText()).findAny().orElse("");
	}

}
