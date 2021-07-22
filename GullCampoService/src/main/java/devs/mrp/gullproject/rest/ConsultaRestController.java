package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.models.ConsultaRepresentationModel;
import devs.mrp.gullproject.domains.models.ConsultaRepresentationModelAssembler;
import devs.mrp.gullproject.domainsdto.ConsultaForSpreadsheet;
import devs.mrp.gullproject.service.ConsultaService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api/consultas", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConsultaRestController {

	private final ConsultaService consultaService;
	private final ConsultaRepresentationModelAssembler crma;
	
	@Autowired
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
	
	@GetMapping(path = "/allforspreadsheet")
	public Flux<ConsultaForSpreadsheet> getallConsultasForSpreadsheet(){
		Flux<Consulta> consultas = consultaService.findAll();
		Flux<ConsultaRepresentationModel> crm = consultas.map(e -> crma.toModel(e));
		Flux<ConsultaForSpreadsheet> cfs = crm.map(e -> ConsultaForSpreadsheet.fromModel(e));
		return cfs;
	}
	
}
