package devs.mrp.gullproject.service.facade;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ConsultaAndLinesFacade {

	@Autowired LineaService lineaService;
	@Autowired ConsultaRepo consultaRepo;
	
	public Mono<Integer> updateAssignedLinesOfProposal(String idPropuesta) {
		return lineaService.findByPropuestaId(idPropuesta)
			.collectList()
			.flatMap(rList -> {
				AtomicInteger count = new AtomicInteger();
				rList.stream().forEach(li -> {
					if (li.getCounterLineId() != null) {
						count.addAndGet(li.getCounterLineId().size());
					}
				});
				log.debug("going to update asigned lines of " + idPropuesta + " to " + count.get());
				return consultaRepo.updateAssignedLinesOfProposal(idPropuesta, count.get())
						.map(rCons -> {
							log.debug("a devolver asignadas");
							int assignadas = ((PropuestaProveedor)rCons.operations().getPropuestaById(idPropuesta)).getLineasAsignadas();
							log.debug("devolviendo asignadas: " + assignadas);
							return assignadas;
						});
			});
	}
	
}
