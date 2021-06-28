package devs.mrp.gullproject.domains;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PvperLinea {

	String pvperId;
	double margen;
	double pvp;
	
	public PvperLinea(PvperLinea l) {
		this.pvperId = l.getPvperId();
		this.margen = l.getMargen();
		this.pvp = l.getPvp();
	}
	
}
