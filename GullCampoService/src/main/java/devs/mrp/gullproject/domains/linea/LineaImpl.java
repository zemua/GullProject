package devs.mrp.gullproject.domains.linea;

import devs.mrp.gullproject.service.linea.LineaOperations;

public class LineaImpl extends Linea {

	public LineaImpl() {
		super();
	}
	
	public LineaImpl(Linea l) {
		super(l);
	}
	
	@Override
	public LineaOperations operations() {
		return new LineaOperations(this);
	}
	
}
