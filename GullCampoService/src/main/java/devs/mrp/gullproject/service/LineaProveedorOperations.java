package devs.mrp.gullproject.service;

import java.util.Iterator;
import java.util.Optional;

import devs.mrp.gullproject.domains.CosteLineaProveedor;
import devs.mrp.gullproject.domains.LineaProveedor;

public class LineaProveedorOperations extends LineaOperations {

	LineaProveedor linea;
	
	public LineaProveedorOperations(LineaProveedor linea) {
		super(linea);
		this.linea = linea;
	}
	
	public CosteLineaProveedor getCosteByCosteId(String costeId) {
		if (linea.getCostesProveedor() == null) {
			return new CosteLineaProveedor(costeId);
		}
		Optional<CosteLineaProveedor> cos = linea.getCostesProveedor().stream().filter(c -> c.getCosteProveedorId().equals(costeId)).findFirst();
		return cos.orElse(new CosteLineaProveedor(costeId));
	}
	
	public boolean ifHasCost(String costId) {
		Optional<CosteLineaProveedor> coste = linea.getCostesProveedor().stream().filter(c -> c.getCosteProveedorId().equals(costId)).findAny();
		return coste.isPresent();
	}
	
	public void removeCosteById(String costId) {
		Iterator<CosteLineaProveedor> it = linea.getCostesProveedor().iterator();
		while (it.hasNext()) {
			var cos = it.next();
			if (cos.getCosteProveedorId().equals(costId)) {
				it.remove();
			}
		}
	}

}
