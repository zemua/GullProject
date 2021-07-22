package devs.mrp.gullproject.service.linea;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyFilter;
import devs.mrp.gullproject.domains.linea.Linea;

@Service
public class LineByProposalFilterFactory implements MyFactoryFromTo<List<Linea>, MyFilter<List<String>, List<Linea>>> {

	@Override
	public MyFilter<List<String>, List<Linea>> from(List<Linea> element) {
		return new LineByProposalFilter(element);
	}

}
