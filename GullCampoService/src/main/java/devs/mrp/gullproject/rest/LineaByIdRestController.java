package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.models.LineaRepresentationModel;
import devs.mrp.gullproject.domains.models.LineaRepresentationModelAssembler;
import devs.mrp.gullproject.service.LineaService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/lineas/id", produces = MediaType.APPLICATION_JSON_VALUE)
public class LineaByIdRestController {
	// TODO test
	
	private final LineaService lineaService;
	private final LineaRepresentationModelAssembler lrma;
	
	@Autowired
	public LineaByIdRestController(LineaService lineaService, LineaRepresentationModelAssembler lrma) {
		this.lineaService = lineaService;
		this.lrma = lrma;
	}
	
	@GetMapping(path = "/{id}")
	public Mono<LineaRepresentationModel> getLineaById(@PathVariable(value = "id") String id) {
		Mono<Linea> linea = lineaService.findById(id);
		Mono<LineaRepresentationModel> lrm = linea.map(e -> lrma.toModel(e));
		return lrm;
	}
	
}
