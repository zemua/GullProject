package devs.mrp.gullproject.domains.dto;

import java.util.Iterator;
import java.util.List;

import devs.mrp.gullproject.domains.Campo;
import lombok.Data;

@Data
public class LineaWithSelectorDto {

	String id;
	String nombre;
	String propuestaId;
	String parentId;
	String counterLineId;
	Integer order;
	List<Campo<?>> campos;
	
	Boolean selected;
	
	public String getValueByAttId(String attId) {
		Campo<?> c = getCampoByAttId(attId);
		if (c == null || c.getDatos() == null) {
			return "";
		} else {
			return String.valueOf(c.getDatos());
		}
	}
	
	public Campo<?> getCampoByAttId(String attId) {
		Iterator<Campo<?>> it = campos.iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(attId)) {
				return campo;
			}
		}
		return null;
	}
	
}
