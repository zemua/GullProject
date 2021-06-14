package devs.mrp.gullproject.service;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.TipoPropuesta;

public class PropuestaOperations {

	private final Propuesta propuesta;
	
	public PropuestaOperations(Propuesta propuesta) {
		this.propuesta = propuesta;
	}
	
	public boolean isCustomer() {
		if (this.propuesta.getTipoPropuesta() == TipoPropuesta.CLIENTE) {
			return true;
		}
		return false;
	}
	
	public boolean isSupplier() {
		if (this.propuesta.getTipoPropuesta() == TipoPropuesta.PROVEEDOR) {
			return true;
		}
		return false;
	}

	public boolean isOurs() {
		if (this.propuesta.getTipoPropuesta() == TipoPropuesta.NUESTRA) {
			return true;
		}
		return false;
	}

	public void addLineaId(String lineaId) {
		this.propuesta.getLineaIds().add(lineaId);
		
	}

	public void addLineaIds(List<String> lineaIds) {
		this.propuesta.getLineaIds().addAll(lineaIds);
		
	}

	public boolean removeLineaId(String lineaId) {
		return this.propuesta.getLineaIds().remove(lineaId);
	}

	public void removeLineaIds(List<String> lineaIds) {
		this.propuesta.getLineaIds().removeAll(lineaIds);
	}

	public List<String> getAllLineaIds() {
		return this.propuesta.getLineaIds();
	}
	
	public int getCantidadLineaIds() {
		return this.propuesta.getLineaIds().size();
	}

	public String getLineaIdByIndex(int index) {
		return this.propuesta.getLineaIds().get(index);
	}

	public boolean updateLineaId(String idOriginal, String idDeseado) {
		ListIterator<String> it = this.propuesta.getLineaIds().listIterator();
		while (it.hasNext()) {
			String li = it.next();
			if(li.equals(idOriginal)) {
				it.set(idDeseado);
				return true;
			}
		}
		return false;
	}

	public boolean updateLineaIdByIndex(int index, String lineaId) {
		return this.propuesta.getLineaIds().set(index, lineaId) != null;
	}
	
	public void setAttributeColumns(List<AtributoForCampo> attributes) {
		this.propuesta.setAttributeColumns(attributes);
	}
	
	public List<AtributoForCampo> getAttributeColumns(){
		return this.propuesta.getAttributeColumns();
	}
	
	public boolean ifHasAttributeColumn(String atributoId) {
		Optional<Boolean> match = this.getAttributeColumns().stream().filter(att -> att.getId().equals(atributoId)).map(att -> true).findFirst();
		return match.orElse(false);
	}
	
	public void addAttribute(AtributoForCampo att) {
		this.propuesta.getAttributeColumns().add(att);
	}
	
	public void removeAttribute(AtributoForCampo att) {
		this.propuesta.getAttributeColumns().remove(att);
	}
	
	public void removeAttributeById(String id) {
		ListIterator<AtributoForCampo> it = this.getAttributeColumns().listIterator();
		while (it.hasNext()) {
			AtributoForCampo at = it.next();
			if (at.getId().equals(id)) {
				it.remove();
				break;
			}
		}
	}
	
}
