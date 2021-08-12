package devs.mrp.gullproject.domains.propuestas;

import org.springframework.data.annotation.PersistenceConstructor;

public class PropuestaCliente extends Propuesta { // TODO add quantities into the views
	
	private int qty;
	
	public int getQty() {
		return qty;
	}
	
	public void setQty(int i) {
		this.qty = i;
	}

	public PropuestaCliente() {
		super(TipoPropuesta.CLIENTE);
	}
	
	public PropuestaCliente(String consultaId) {
		super(TipoPropuesta.CLIENTE);
		this.setForProposalId(consultaId);
	}
	
	@PersistenceConstructor
	public PropuestaCliente(TipoPropuesta tipoPropuesta) { // dummy parameter for Thymeleaf
		super(TipoPropuesta.CLIENTE);
	}
	
	public PropuestaCliente(Propuesta propuesta) {
		super(TipoPropuesta.CLIENTE);
		this.attributeColumns = propuesta.attributeColumns;
		this.forProposalId = propuesta.forProposalId;
		this.id = propuesta.id;
		this.lineaIds = propuesta.lineaIds;
		this.nombre = propuesta.nombre;
		this.parentId = propuesta.parentId;
	}

}
