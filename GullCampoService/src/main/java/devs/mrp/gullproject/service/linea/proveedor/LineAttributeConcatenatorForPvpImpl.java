package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.Pvper;

public class LineAttributeConcatenatorForPvpImpl implements LineAttributeConcatenatorForPvp {

	private LinesMapperByCounterId linesMapper;
	private Map<String, Pvper> pvperMapper;
	
	public LineAttributeConcatenatorForPvpImpl(LinesMapperByCounterId linesMapper, List<Pvper> pvps) {
		this.linesMapper = linesMapper;
		this.pvperMapper = pvps.stream().collect(Collectors.toMap((pvp) -> pvp.getId(), (pvp) -> pvp));
	}
	
	@Override
	public String forLineAttPvp(String counterLineId, String attId, String pvpId) { // TODO
		List<Linea> lineas = linesMapper.forCounter(counterLineId);
		
		Pvper pvp = pvperMapper.get(pvpId);
		//List<String> cotizaciones = pvp.get;
		pvperMapper.get(pvpId).getIdAttributesByCotiz();
		return null;
	}

}
