package devs.mrp.gullproject.afactories;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.service.LineByAssignationRetriever;
import devs.mrp.gullproject.service.PvpMarginMapperByAssignedLine;

@Service
public class PvpMarginMapperByLineFactory implements MyFactoryFromTo<List<Linea>, MyMapperByDupla<Double, String, String>> {

	List<Linea> list;
	
	public PvpMarginMapperByLineFactory(List<Linea> list) {
		this.list = list;
	}
	
	@Override
	public MyMapperByDupla<Double, String, String> from(List<Linea> element) {
		MyListOfAsignables<Linea> l = new LineByAssignationRetriever(element);
		MyMapperByDupla<Double, String, String> mapper = new PvpMarginMapperByAssignedLine(l);
		return mapper;
	}

}
