package devs.mrp.gullproject.service;

import devs.mrp.gullproject.ainterfaces.MyParameterizedById;
import devs.mrp.gullproject.domains.CosteLineaProveedor;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.LineaProveedor;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import lombok.Data;

public class LineCostRetriever implements MyParameterizedById<Double> {
	
	LineaProveedor linea;

	LineCostRetriever(LineaProveedor linea) {
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
