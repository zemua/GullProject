package devs.mrp.gullproject.afactories;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.service.LineByAssignationRetriever;
import devs.mrp.gullproject.service.PvpMapperByAssignedLine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PvpMapperByLineFactory implements MyFactoryFromTo<List<Linea>, MyMapperByDupla<Double, String, String>> {

	@Override
	public MyMapperByDupla<Double, String, String> from(List<Linea> element) {
		MyListOfAsignables<Linea> asignables = new LineByAssignationRetriever(element);
		log.debug("asignables: " + asignables.toString());
		MyMapperByDupla<Double, String, String> mapper = new PvpMapperByAssignedLine(asignables);
		log.debug("mapper: " + mapper.toString());
		return mapper;
	}

}
