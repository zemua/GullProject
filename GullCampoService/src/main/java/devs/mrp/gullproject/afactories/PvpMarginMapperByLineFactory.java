package devs.mrp.gullproject.afactories;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.ListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MapperByDupla;
import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.service.LineByAssignationRetriever;
import devs.mrp.gullproject.service.PvpMarginMapperByAssignedLine;

@Service
public class PvpMarginMapperByLineFactory implements MyFactoryFromTo<List<Linea>, MapperByDupla<Double, String, String>> {

	List<Linea> list;
	
	public PvpMarginMapperByLineFactory(List<Linea> list) {
		this.list = list;
	}
	
	@Override
	public MapperByDupla<Double, String, String> from(List<Linea> element) {
		ListOfAsignables<Linea> l = new LineByAssignationRetriever(element);
		MapperByDupla<Double, String, String> mapper = new PvpMarginMapperByAssignedLine(l);
		return mapper;
	}

}
