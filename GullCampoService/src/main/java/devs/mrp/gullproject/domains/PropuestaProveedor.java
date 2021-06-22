package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.PersistenceConstructor;

import devs.mrp.gullproject.service.PropuestaProveedorOperations;
import lombok.Data;
import lombok.NoArgsConstructor;


public class PropuestaProveedor extends Propuesta {
	
	List<CosteProveedor> costes;
	
	public void setCostes(List<CosteProveedor> costes) {
		this.costes = costes;
	}
	
	public List<CosteProveedor> getCostes() {
		return this.costes;
	}

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
	
	public PropuestaProveedor(Propuesta propuesta) throws Exception {
		super(propuesta);
		if (!(propuesta instanceof PropuestaProveedor)) {
			throw new Exception();
		}
		this.costes = ((PropuestaProveedor)propuesta).costes;
	}
	
	public PropuestaProveedorOperations operationsProveedor() {
		return new PropuestaProveedorOperations(this);
	}

}
