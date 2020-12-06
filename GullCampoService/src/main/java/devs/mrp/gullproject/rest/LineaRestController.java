package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.models.LineaRepresentationModel;
import devs.mrp.gullproject.domains.models.LineaRepresentationModelAssembler;
import devs.mrp.gullproject.service.LineaService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api/lineas", produces = MediaType.APPLICATION_JSON_VALUE)
public class LineaRestController {
	
	private final LineaService lineaService;
	private final LineaRepresentationModelAssembler lrma;
	
	@Autowired
	public LineaRestController(LineaService lineaService, LineaRepresentationModelAssembler lrma) {
		this.lineaService = lineaService;
		this.lrma = lrma;
	}
	
	@GetMapping(path = "/all")
	public Flux<LineaRepresentationModel> getAllLineas(){
		Flux<Linea> lineas = lineaService.findAll();
		Flux<LineaRepresentationModel> lrm = lineas.map(e -> lrma.toModel(e));
		
		return lrm;
	}
	
}
