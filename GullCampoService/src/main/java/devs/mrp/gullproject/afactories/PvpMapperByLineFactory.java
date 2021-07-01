package devs.mrp.gullproject.afactories;

import java.util.List;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.ListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MapperByDupla;
import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.service.LineByAssignationRetriever;
import devs.mrp.gullproject.service.PvpMapperByAssignedLine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PvpMapperByLineFactory implements MyFactoryFromTo<List<Linea>, MapperByDupla<Double, String, String>> {

	@Override
	public MapperByDupla<Double, String, String> from(List<Linea> element) { // TODO test
		ListOfAsignables<Linea> asignables = new LineByAssignationRetriever(element);
		log.debug("asignables: " + asignables.toString());
		MapperByDupla<Double, String, String> mapper = new PvpMapperByAssignedLine(asignables);
		log.debug("mapper: " + mapper.toString());
		return mapper;
	}

}
