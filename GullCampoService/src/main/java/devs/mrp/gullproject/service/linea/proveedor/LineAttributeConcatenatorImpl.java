package devs.mrp.gullproject.service.linea.proveedor;

import devs.mrp.gullproject.domains.linea.Linea;

public class LineAttributeConcatenatorImpl implements LineAttributeConcatenator {

	private LinesMapperByCounterId linesMapper;
	
	public LineAttributeConcatenatorImpl(LinesMapperByCounterId linesMapper) {
		this.linesMapper = linesMapper;
	}
	
	@Override
	public String forLineAtt(String counterLineId, String attId) {
		StringBuilder builder = new StringBuilder();
		linesMapper.forCounter(counterLineId)
			.stream().forEach(l -> {
				var appender = getAtt(l, attId);
				if (!appender.isEmpty() && !builder.toString().isEmpty()) {
					builder.append(" / ");
				}
				builder.append(appender);
			});
		return builder.toString();
	}
	
	private String getAtt(Linea linea, String attId) {
		return linea.getCampos().stream().filter(c -> c.getAtributoId().equals(attId)).map(c -> c.getDatosText()).findAny().orElse("");
	}

}
