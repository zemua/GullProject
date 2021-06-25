package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.PersistenceConstructor;

public class PropuestaNuestra extends Propuesta {
	
	List<Pvper> pvps = new ArrayList<>();
	List<PvperSum> sums = new ArrayList<>();
	
	public List<Pvper> getPvps() {
		return this.pvps;
	}
	
	public void setPvps(List<Pvper> pvps) {
		this.pvps = pvps;
	}
	
	public List<PvperSum> getSums() {
		return this.sums;
	}
	
	public void setSums(List<PvperSum> sums) {
		this.sums = sums;
	}

	public PropuestaNuestra() {
		super(TipoPropuesta.NUESTRA);
	}
	
	@PersistenceConstructor
	public PropuestaNuestra(TipoPropuesta tipoPropuesta) { // dummy parameter
		super(TipoPropuesta.NUESTRA);
	}
	
	public PropuestaNuestra(String customerProposalId) {
		super(TipoPropuesta.NUESTRA);
		this.setForProposalId(customerProposalId);
	}
	
	public PropuestaNuestra(Propuesta propuesta) {
		super(TipoPropuesta.NUESTRA);
		this.attributeColumns = propuesta.attributeColumns;
		this.forProposalId = propuesta.forProposalId;
		this.id = propuesta.id;
		this.lineaIds = propuesta.lineaIds;
		this.nombre = propuesta.nombre;
		this.parentId = propuesta.parentId;
	}

}
