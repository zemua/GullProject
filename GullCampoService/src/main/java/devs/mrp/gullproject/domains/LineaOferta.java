package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.gullproject.service.LineaOfertaOperations;

public class LineaOferta extends Linea {

	private List<PvperLinea> pvps;
	private List<String> pvpSums;
	
	public List<PvperLinea> getPvps() {
		return pvps;
	}
	public void setPvps(List<PvperLinea> pvps) {
		this.pvps = pvps;
	}
	public List<String> getPvpSums() {
		return pvpSums;
	}
	public void setPvpSums(List<String> pvpSums) {
		this.pvpSums = pvpSums;
	}
	
	public LineaOferta() {
		
	}
	
	public LineaOferta(LineaOferta lin) {
		super(lin);
		if (lin.getPvpSums() != null) {
			this.pvpSums = new ArrayList<>();
			lin.getPvpSums().stream().forEach(s -> this.pvpSums.add(s));
		}
		if (lin.getPvps() != null) {
			this.pvps = new ArrayList<>();
			lin.getPvps().stream().forEach(p -> this.pvps.add(new PvperLinea(p)));
		}
	}
	
	public LineaOferta(Linea lin) {
		super(lin);
		pvpSums = new ArrayList<>();
		pvps = new ArrayList<>();
	}
	
	public LineaOfertaOperations operations() {
		return new LineaOfertaOperations(this);
	}
	
}
