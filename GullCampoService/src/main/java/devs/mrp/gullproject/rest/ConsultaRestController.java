package devs.mrp.gullproject.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.models.CampoRepresentationModel;
import devs.mrp.gullproject.domains.models.CampoRepresentationModelAssembler;
import devs.mrp.gullproject.domains.models.ConsultaRepresentationModel;
import devs.mrp.gullproject.domains.models.ConsultaRepresentationModelAssembler;
import devs.mrp.gullproject.service.CampoService;
import devs.mrp.gullproject.service.ConsultaService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api/consultas", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConsultaRestController {

	private final ConsultaService consultaService;
	private final ConsultaRepresentationModelAssembler crma;
	
	public ConsultaRestController(ConsultaService consultaService, ConsultaRepresentationModelAssembler crma) {
		this.consultaService = consultaService;
		this.crma = crma;
	}
	
	@GetMapping(path = "/all")
	public Flux<ConsultaRepresentationModel> getallConsultas(){
		Flux<Consulta> consultas = consultaService.findAll();
		Flux<ConsultaRepresentationModel> crm = consultas.map(e -> crma.toModel(e));
		return crm;
	}
	
}
