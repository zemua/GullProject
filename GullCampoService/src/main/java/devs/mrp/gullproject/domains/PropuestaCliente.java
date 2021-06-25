package devs.mrp.gullproject.domains;

import org.springframework.data.annotation.PersistenceConstructor;

public class PropuestaCliente extends Propuesta {

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
