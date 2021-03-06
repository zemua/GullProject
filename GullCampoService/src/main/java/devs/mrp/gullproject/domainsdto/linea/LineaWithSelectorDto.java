package devs.mrp.gullproject.domainsdto.linea;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
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
	List<CosteLineaProveedor> costesProveedor;
	
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
	
	public CosteLineaProveedor getCosteByCosteId(String costeId) {
		Optional<CosteLineaProveedor> csot = costesProveedor.stream().filter(c -> {
			var cos = c.getCosteProveedorId();
			if (cos == null) { return false; }
			return c.getCosteProveedorId().equals(costeId);
		}).findAny();
		return csot.orElse(new CosteLineaProveedor(costeId));
	}
	
}
