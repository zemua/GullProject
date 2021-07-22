package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.IdName;
import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.service.propuesta.proveedor.ProposalCostNameMapperFromPvp;

public interface TotalCostOfAllLinesFinderFactory {

	public TotalCostOfAllLinesFinder from(MyFinder<List<IdName>, String> proposalCostMapper, MyMapperByDupla<Double, String, String> lineCostMapper, List<String> counterLineIds);
	
}
