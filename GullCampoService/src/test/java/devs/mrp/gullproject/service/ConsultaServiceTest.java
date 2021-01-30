package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.CustomConsultaRepo;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ConsultaServiceTest {

	ConsultaService consultaService;
	ConsultaRepo consultaRepo;
	
	@Autowired
	public ConsultaServiceTest(ConsultaService consultaService, ConsultaRepo consultaRepo) {
		this.consultaService = consultaService;
		this.consultaRepo = consultaRepo;
		if (!(consultaRepo instanceof CustomConsultaRepo)) {
			fail("ConsultaRepo no extiende CustomConsultaRepo");
		}
	}
	
	Consulta consulta;
	Propuesta propuesta1;
	Propuesta propuesta2;
	Mono<Consulta> mono;
	
	@BeforeEach
	void init() {
		consulta = new Consulta();
		propuesta1 = new PropuestaCliente();
		propuesta2 = new PropuestaCliente();
		
		consulta.addPropuesta(propuesta1);
		consulta.addPropuesta(propuesta2);
		
		consultaRepo.save(consulta).block();
		
		mono = consultaRepo.findById(consulta.getId());
		
		StepVerifier.create(mono)
			.assertNext(cons -> {
				assertEquals(2, cons.getCantidadPropuestas());
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	void testRemovePropuestaById() {
		
		Integer cant = consultaService.removePropuestaById(consulta.getId(), propuesta1.getId()).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals(1, cons.getCantidadPropuestas());
			assertEquals(propuesta2.getId(), cons.getPropuestaByIndex(0).getId());
			assertEquals(1, cant);
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testFindPropuestaByPropuestaId() {
		
		Mono<Propuesta> mono = consultaService.findPropuestaByPropuestaId(propuesta1.getId());
		
		StepVerifier.create(mono)
		.assertNext(prop -> {
			assertEquals(propuesta1, prop);
		})
		.expectComplete()
		.verify();
	}

}
