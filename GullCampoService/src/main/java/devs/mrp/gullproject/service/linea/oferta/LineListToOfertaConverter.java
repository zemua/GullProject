package devs.mrp.gullproject.service.linea.oferta;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.afactories.LineToOfferLineFactory;
import devs.mrp.gullproject.ainterfaces.MyConverter;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.LineaOferta;

@Service
public class LineListToOfertaConverter implements MyConverter<List<Linea>, List<LineaOferta>> {

	@Autowired LineToOfferLineFactory factory;
	
	@Override
	public List<LineaOferta> convert(List<Linea> input) {
		return input.stream().map(l -> factory.from(l)).collect(Collectors.toList());
	}

}
