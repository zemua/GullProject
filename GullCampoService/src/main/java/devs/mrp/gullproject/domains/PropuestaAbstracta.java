package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public abstract class PropuestaAbstracta implements Propuesta {
	
	@Id
	String id = new ObjectId().toString();
	
	//private TipoPropuesta tipoPropuesta;
	
	@NotBlank(message = "Selecciona un nombre")
	String nombre;
	
	/**
	 * parentId refers to the "previous version" of this proposal
	 */
	String parentId;
	
	List<String> lineaIds = new ArrayList<>();
	//List<String> atributoIds = new ArrayList<>();
	List<AtributoForCampo> attributeColumns = new ArrayList<>();

	@Override
	public abstract boolean isRoot();

	@Override
	public abstract boolean isForBid();

	@Override
	public void addLineaId(String lineaId) {
		lineaIds.add(lineaId);
		
	}

	@Override
	public void addLineaIds(List<String> lineaIds) {
		lineaIds.addAll(lineaIds);
		
	}

	@Override
	public boolean removeLineaId(String lineaId) {
		return lineaIds.remove(lineaId);
	}

	@Override
	public void removeLineaIds(List<String> lineaIds) {
		lineaIds.removeAll(lineaIds);
	}

	@Override
	public List<String> getAllLineaIds() {
		return lineaIds;
	}
	
	@Override
	public int getCantidadLineaIds() {
		return lineaIds.size();
	}

	@Override
	public String getLineaIdByIndex(int index) {
		return lineaIds.get(index);
	}

	@Override
	public boolean updateLineaId(String idOriginal, String idDeseado) {
		ListIterator<String> it = lineaIds.listIterator();
		while (it.hasNext()) {
			String li = it.next();
			if(li.equals(idOriginal)) {
				it.set(idDeseado);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean updateLineaIdByIndex(int index, String lineaId) {
		return lineaIds.set(index, lineaId) != null;
	}
	
	public void setAttributeColumns(List<AtributoForCampo> attributes) {
		attributeColumns = attributes;
	}
	
	public List<AtributoForCampo> getAttributeColumns(){
		return attributeColumns;
	}
	
	@Override
	public void addAttribute(AtributoForCampo att) {
		attributeColumns.add(att);
	}
	
	@Override
	public void removeAttribute(AtributoForCampo att) {
		attributeColumns.remove(att);
	}
	
	@Override
	public void removeAttributeById(String id) {
		ListIterator<AtributoForCampo> it = attributeColumns.listIterator();
		while (it.hasNext()) {
			AtributoForCampo at = it.next();
			if (at.getId().equals(id)) {
				it.remove();
				break;
			}
		}
	}
}
