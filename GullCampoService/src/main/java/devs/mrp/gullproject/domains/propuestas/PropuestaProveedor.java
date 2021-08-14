package devs.mrp.gullproject.domains.propuestas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.PersistenceConstructor;

import devs.mrp.gullproject.service.propuesta.proveedor.PropuestaProveedorOperations;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropuestaProveedor extends Propuesta {
	
	List<CosteProveedor> costes;
	int lineasAsignadas;
	
	public void setLineasAsignadas(int n) {
		this.lineasAsignadas = n;
	}
	
	public int getLineasAsignadas() {
		return this.lineasAsignadas;
	}
	
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
	
	public PropuestaProveedor(Propuesta propuesta) {
		super(TipoPropuesta.PROVEEDOR);
		this.id = propuesta.id;
		this.attributeColumns = propuesta.attributeColumns;
		this.forProposalId = propuesta.forProposalId;
		this.lineaIds = propuesta.lineaIds;
		this.nombre = propuesta.nombre;
		this.parentId = propuesta.parentId;
		//if (propuesta instanceof PropuestaProveedor) {
		if (propuesta.getTipoPropuesta().equals(TipoPropuesta.PROVEEDOR)) {
			log.debug("es propuesta proveedor");
			this.costes = ((PropuestaProveedor)propuesta).costes; // no separate references
			this.lineasAsignadas = ((PropuestaProveedor)propuesta).lineasAsignadas;
		}
	}
	
	public PropuestaProveedorOperations operationsProveedor() {
		return new PropuestaProveedorOperations(this);
	}

}
