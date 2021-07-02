package devs.mrp.gullproject.service.linea.proveedor;

import devs.mrp.gullproject.ainterfaces.MyParameterizedById;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaProveedor;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import lombok.Data;

public class LineCostRetriever implements MyParameterizedById<Double> {
	
	LineaProveedor linea;

	public LineCostRetriever(LineaProveedor linea) {
		this.linea = linea;
	}
	
	@Override
	public Double getParameter(String id) {
		return linea.getCostesProveedor().stream()
				.filter(c -> c.getCosteProveedorId().equals(id))
				.map(c -> c.getValue())
				.findAny().orElse(0D);
	}

}
