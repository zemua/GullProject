package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.consultaAndLineFacade.FacadeInitialization;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CompoundedConsultaLineaServiceTest extends FacadeInitialization {

	@Autowired CompoundedConsultaLineaService service;
	
	@Test
	void testRemovePropuestasAssignedToAndTheirLines() {
		service.removePropuestasAssignedToAndTheirLines(consulta.getId(), p1.getId()).block();
		
		StepVerifier.create(monoConsulta)
		.assertNext(cons -> {
			assertEquals(1, cons.operations().getCantidadPropuestas());
			assertEquals(p1.getId(), cons.getPropuestas().get(0).getId());
		})
		.expectComplete()
		.verify()
		;
		
		StepVerifier.create(fluxLineas)
		.assertNext(l -> {
			assertEquals(l6.getId(), l.getId());
		})
		.expectComplete()
		.verify()
		;
	}
	
}
