package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import devs.mrp.gullproject.ainterfaces.IdName;
import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.service.propuesta.proveedor.ProposalCostNameMapperFromPvp;

public class TotalCostOfAllLinesFinderImpl implements TotalCostOfAllLinesFinder { // TODO test

	private MyFinder<List<IdName>, String> proposalCostMapper;
	private MyMapperByDupla<Double, String, String> lineCostMapper;
	private List<String> counterLineIds;
	
	public TotalCostOfAllLinesFinderImpl(MyFinder<List<IdName>, String> proposalCostMapper, MyMapperByDupla<Double, String, String> lineCostMapper, List<String> counterLineIds) {
		this.proposalCostMapper = proposalCostMapper;
		this.lineCostMapper = lineCostMapper;
		this.counterLineIds = counterLineIds;
	}
	
	@Override
	public double forPvp(String pvpId) {
		var doubleAdder = new DoubleAdder();
		var costIds = proposalCostMapper.findBy(pvpId).stream().map(c -> c.getId()).collect(Collectors.toList());
		counterLineIds.forEach(line -> {
			costIds.forEach(cost -> {
				doubleAdder.add(lineCostMapper.getByDupla(line, cost));
			});
		});
		return doubleAdder.doubleValue();
	}

}
