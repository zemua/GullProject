package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.IdName;
import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.service.propuesta.proveedor.ProposalCostNameMapperFromPvp;

@Service
public class TotalCostOfAllLinesFinderFactoryImpl implements TotalCostOfAllLinesFinderFactory {

	@Override
	public TotalCostOfAllLinesFinder from(MyFinder<List<IdName>, String> proposalCostMapper,
			MyMapperByDupla<Double, String, String> lineCostMapper, List<String> counterLineIds) {
		return new TotalCostOfAllLinesFinderImpl(proposalCostMapper, lineCostMapper, counterLineIds);
	}

}
