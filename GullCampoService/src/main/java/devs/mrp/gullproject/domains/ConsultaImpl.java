package devs.mrp.gullproject.domains;

import devs.mrp.gullproject.service.ConsultaOperations;

public class ConsultaImpl extends Consulta {

	@Override
	public ConsultaOperations operations() {
		return new ConsultaOperations(this);
	}
	
}
