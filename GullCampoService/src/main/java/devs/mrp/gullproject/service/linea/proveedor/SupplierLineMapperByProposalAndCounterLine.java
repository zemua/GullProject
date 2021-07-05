package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;

public class SupplierLineMapperByProposalAndCounterLine implements MyMapperByDupla<Linea, String, String> {

	private List<Linea> lineas;
	
	public SupplierLineMapperByProposalAndCounterLine(List<Linea> lineas) {
		this.lineas = lineas;
	}
	
	@Override
	public Linea getByDupla(String proposalProveedorId, String customerCounterLineId) {
		return lineas.stream()
				.filter(l -> l.getPropuestaId().equals(proposalProveedorId))
				.filter(l -> l.operations().ifAssignedTo(customerCounterLineId))
				.findAny().orElse(null);
	}	
	
}
