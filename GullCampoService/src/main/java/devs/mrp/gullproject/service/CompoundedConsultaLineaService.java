package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;

import devs.mrp.gullproject.domains.Linea;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
public class CompoundedConsultaLineaService {

	ConsultaService consultaService;
	LineaService lineaService;
	
	@Autowired
	public CompoundedConsultaLineaService(ConsultaService consultaService, LineaService lineaService) {
		this.consultaService = consultaService;
		this.lineaService = lineaService;
	}
	
	public Flux<Linea> getAllLineasOfPropuestasAssignedTo(String propuestaClienteId) {
		return consultaService.getAllPropuestaProveedorAsignedTo(propuestaClienteId)
			.flatMap(rProp -> lineaService.findByPropuestaId(rProp.getId()));
	}
	
	public Mono<Void> removePropuestasAssignedToAndTheirLines(String idConsulta, String idAssignedTo) { // TODO test
		return consultaService.removePropuestasByAssignedtoAndReturnOld(idConsulta, idAssignedTo)
				.flatMapMany(rConsulta -> {
					return Flux.fromIterable(rConsulta.operations().getAllPropuestasAssignedToId(idAssignedTo))
							.map(rProp -> {
								return lineaService.deleteSeveralLineasFromPropuestaId(rProp.getId());
							});
				})
				.then(Mono.empty());
	}
	
}
