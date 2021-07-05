package devs.mrp.gullproject.service.linea.oferta;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.service.linea.LineByAssignationRetrieverFactory;

public class PvpMarginMapperByAssignedLineFactory implements PvpMarginMapperByCounterIdFactory {

	@Autowired LineByAssignationRetrieverFactory<Linea> factory;
	
	@Override
	public MyMapperByDupla<Double, String, String> from(List<Linea> lineas) {
		return new PvpMarginMapperByAssignedLine(factory.from(lineas));
	}

}
