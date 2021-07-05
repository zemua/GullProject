package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;

public class SupplierLineMapperByProposalAndCounterLineFactory implements SupplierLineMapperByPropAndAssignedLineFactory {

	@Override
	public MyMapperByDupla<Linea, String, String> from(List<Linea> lineas) {
		return new SupplierLineMapperByProposalAndCounterLine(lineas);
	}

}
