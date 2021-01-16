package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.models.ConsultaRepresentationModel;
import devs.mrp.gullproject.domains.models.ConsultaRepresentationModelAssembler;
import devs.mrp.gullproject.service.ConsultaService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/consultas/id", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConsultaByIdRestController {
	
	private final ConsultaService consultaService;
	private final ConsultaRepresentationModelAssembler crma;
	
	@Autowired
	public ConsultaByIdRestController(ConsultaService consultaService, ConsultaRepresentationModelAssembler crma) {
		this.consultaService = consultaService;
		this.crma = crma;
	}
	
	@GetMapping(path = "/{id}")
	public Mono<ConsultaRepresentationModel> getConsultaById(@PathVariable(value = "id") String id) {
		Mono<Consulta> consulta = consultaService.findById(id);
		Mono<ConsultaRepresentationModel> crm = consulta.map(e -> crma.toModel(e));
		return crm;
	}

}
