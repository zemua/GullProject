package devs.mrp.gullproject.service;

import devs.mrp.gullproject.ainterfaces.ParametrizableById;
import devs.mrp.gullproject.domains.CosteLineaProveedor;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import lombok.Data;

public class LineCostRetriever implements ParametrizableById<Double> {
	
	Linea linea;

	LineCostRetriever(Linea linea) {
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
