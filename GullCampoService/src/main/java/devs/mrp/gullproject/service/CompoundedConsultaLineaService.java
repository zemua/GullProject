package devs.mrp.gullproject.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CompoundedConsultaLineaService {

	ConsultaService consultaService;
	LineaService lineaService;
	LineaRepo lineaRepo;
	
	@Autowired
	public CompoundedConsultaLineaService(ConsultaService consultaService, LineaService lineaService, LineaRepo lineaRepo) {
		this.consultaService = consultaService;
		this.lineaService = lineaService;
		this.lineaRepo = lineaRepo;
	}
	
	public Flux<Linea> getAllLineasOfPropuestasAssignedTo(String propuestaClienteId) {
		return consultaService.getAllPropuestaProveedorAsignedTo(propuestaClienteId)
			.flatMap(rProp -> lineaService.findByPropuestaId(rProp.getId()));
	}
	
	@Transactional
	public Mono<Void> removePropuestasAssignedToAndTheirLines(String idConsulta, String idAssignedTo) {
		return consultaService.removePropuestasByAssignedtoAndReturnOld(idConsulta, idAssignedTo)
				.flatMap(rConsulta -> {
					var lista = rConsulta.operations().getAllPropuestasAssignedToId(idAssignedTo).stream().map(p -> p.getId()).collect(Collectors.toList());
					log.debug("to delete lines from these proposalIds " + lista.toString());
					return lineaRepo.deleteSeveralLineasBySeveralPropuestaIds(lista);
				})
				.then(Mono.empty());
	}
	
}
