package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.PersistenceConstructor;

import devs.mrp.gullproject.service.PropuestaProveedorOperations;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PropuestaProveedor extends Propuesta {
	
	List<CosteProveedor> costes;

	public PropuestaProveedor() {
		super(TipoPropuesta.PROVEEDOR);
	}
	
	@PersistenceConstructor
	public PropuestaProveedor(TipoPropuesta tipoPropuesta) { // dummy parameter for Thymeleaf
		super(TipoPropuesta.PROVEEDOR);
	}
	
	public PropuestaProveedor(String idPropuestaCliente) {
		super(TipoPropuesta.PROVEEDOR);
		this.setForProposalId(idPropuestaCliente);
	}
	
	public PropuestaProveedor(Propuesta propuesta) {
		super(TipoPropuesta.PROVEEDOR);
		this.attributeColumns = propuesta.attributeColumns;
		this.forProposalId = propuesta.forProposalId;
		this.id = propuesta.id;
		this.lineaIds = propuesta.lineaIds;
		this.nombre = propuesta.nombre;
		this.parentId = propuesta.parentId;
		if (propuesta instanceof PropuestaProveedor) {
			this.costes = ((PropuestaProveedor)propuesta).costes;
		} else {
			this.costes = new ArrayList<>();
		}
	}
	
	public PropuestaProveedorOperations operationsProveedor() {
		return new PropuestaProveedorOperations(this);
	}

}
