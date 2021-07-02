package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.models.LineaRepresentationModel;
import devs.mrp.gullproject.domains.models.LineaRepresentationModelAssembler;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
	
	@PostMapping(path = "/nueva", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<LineaRepresentationModel> crearLinea(@RequestBody Linea linea){
		Mono<Linea> entity = lineaService.addLinea(linea);
		Mono<LineaRepresentationModel> lrm = entity.map(e -> lrma.toModel(e));
		return lrm;
	}
	
	@PutMapping(path = "/actualizar-una", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<LineaRepresentationModel> actualizarLinea(@RequestBody Linea linea) {
		Mono<Linea> entity = lineaService.updateLinea(linea);
		Mono<LineaRepresentationModel> lrm = entity.map(e -> lrma.toModel(e));
		return lrm;
	}
	
	@DeleteMapping(path = "/borrar-una")
	public Mono<Long> borrarLineaById(@RequestParam(name = "id") String id) {
		Mono<Long> borradas = lineaService.deleteLineaById(id);
		return borradas;
	}
	
	
}
