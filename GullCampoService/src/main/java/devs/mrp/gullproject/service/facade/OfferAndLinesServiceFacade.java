package devs.mrp.gullproject.service.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaOfferService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OfferAndLinesServiceFacade {

	@Autowired LineaOfferService lineaService;
	@Autowired ConsultaService consultaService;
	
	public Mono<Void> clearAllLinesOfOffer(String offerId) { // TODO test
		return lineaService.clearAllLinesOfOferta(offerId)
				.then(consultaService.updateLineasDePropuesta(offerId, new ArrayList<>()))
				.then();
	}
	
	public Flux<LineaAbstracta> saveAll(String offerId, List<LineaAbstracta> lineas) { // TODO test
		List<String> lineIds = lineas.stream().map(l -> l.getId()).collect(Collectors.toList());
		return consultaService.updateLineasDePropuesta(offerId, lineIds)
				.thenMany(lineaService.saveAll(lineas));
	}
	
}
