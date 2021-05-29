package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.CustomLineaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LineaServiceTest {
	
	LineaService lineaService;
	LineaRepo lineaRepo;
	ConsultaRepo consultaRepo;
	
	@Autowired
	public LineaServiceTest(LineaService lineaService, LineaRepo lineaRepo, ConsultaRepo consultaRepo) {
		this.lineaService = lineaService;
		this.lineaRepo = lineaRepo;
		if (!(lineaRepo instanceof CustomLineaRepo)) {
			fail("LineaRepo no extiende CustomLineaRepo");
		}
		this.consultaRepo = consultaRepo;
	}
	
	Campo<Integer> campo1;
	Campo<String> campo2;
	Campo<String> campo3;
	Linea linea1;
	Linea linea2;
	Linea linea3;
	Linea linea4;
	Linea linea5;
	Linea linea6;
	Linea linea7;
	Mono<Linea> mono;
	Flux<Linea> flux;
	
	Consulta consulta;
	PropuestaCliente propuesta;
	PropuestaCliente propuesta2;
	Mono<Consulta> monoConsulta;
	
	@BeforeEach
	void setUp() {
		lineaRepo.deleteAll().block();
			
		linea1 = new Linea();
		linea1.setId("id1");
		linea1.setNombre("name1");
		linea1.setOrder(1);
		linea1.setParentId("parent id 1");
		linea1.setCounterLineId("counter line id 1");
		
		lineaRepo.save(linea1).block();
		
		linea2 = new Linea();
		linea2.setId("id2");
		linea2.setNombre("name2");
		linea2.setOrder(2);
		linea2.setParentId("parent id 2");
		linea2.setCounterLineId("counter line id 2");
		
		lineaRepo.save(linea2).block();
		
		mono = lineaRepo.findById("id1");
		flux = lineaRepo.findAll();
		
		StepVerifier.create(flux)
			.assertNext(line -> {
				assertEquals("id1", line.getId());
				assertEquals(1, line.getOrder());
			})
			.assertNext(line -> {
				assertEquals("id2", line.getId());
				assertEquals(2, line.getOrder());
			})
			.expectComplete()
			.verify();
		
		consultaRepo.deleteAll().block();
		
		consulta = new Consulta();
		propuesta = new PropuestaCliente();
		propuesta2 = new PropuestaCliente();
		consulta.addPropuesta(propuesta);
		consulta.addPropuesta(propuesta2);
		consultaRepo.save(consulta).block();
		
		linea3 = new Linea(); linea3.setNombre("linea3"); linea3.setPropuestaId(propuesta.getId());
		linea4 = new Linea(); linea4.setNombre("linea4"); linea4.setPropuestaId(propuesta.getId());
		linea5 = new Linea(); linea5.setNombre("linea5"); linea5.setPropuestaId(propuesta.getId());
		linea6 = new Linea(); linea6.setNombre("linea6"); linea6.setPropuestaId(propuesta2.getId());
		linea7 = new Linea(); linea7.setNombre("linea7"); linea7.setPropuestaId(propuesta2.getId());
		
		lineaService.addLinea(linea3).block();
		
		monoConsulta = consultaRepo.findById(consulta.getId());
		StepVerifier.create(monoConsulta)
			.assertNext(c -> {
				assertEquals(consulta.getId(), c.getId());
				assertEquals(2, c.getCantidadPropuestas());
				assertEquals(propuesta.getId(), c.getPropuestaByIndex(0).getId());
				assertEquals(1, c.getPropuestaByIndex(0).getCantidadLineaIds());
				assertEquals(linea3.getId(),c.getPropuestaByIndex(0).getLineaIdByIndex(0));
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	void testUpdateOrderOfSeveralLineas() throws InterruptedException {
		Map<String, Integer> idVSposicion = new HashMap<>();
		
		idVSposicion.put("id1", 2);
		idVSposicion.put("id2", 0);
		
		lineaService.updateOrderOfSeveralLineas(idVSposicion).block();
		Thread.sleep(500);
		
		StepVerifier.create(flux)
		.assertNext(line -> {
			assertEquals("id1", line.getId());
			assertEquals(2, line.getOrder());
		})
		.assertNext(line -> {
			assertEquals("id2", line.getId());
			assertEquals(0, line.getOrder());
		})
		.assertNext(line -> {
			assertEquals(linea3.getId(), line.getId());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testAddLinea_And_DeleteLineaById() {
		Linea lineaz = new Linea();
		lineaz.setPropuestaId(propuesta.getId());
		lineaService.addLinea(lineaz).block();
		
		Mono<Linea> monoLinea = lineaService.findById(lineaz.getId());
		StepVerifier.create(monoLinea)
			.assertNext(line -> {
				assertEquals(lineaz.getId(), line.getId());
				assertEquals(2, line.getOrder());
			})
			.expectComplete()
			.verify();
		
		StepVerifier.create(monoConsulta)
			.assertNext(cons -> {
				assertEquals(2, cons.getPropuestaByIndex(0).getCantidadLineaIds());
				assertEquals(lineaz.getId(), cons.getPropuestaByIndex(0).getLineaIdByIndex(1));
			})
			.expectComplete()
			.verify();
		
		lineaService.deleteLineaById(lineaz.getId()).block();
		
		StepVerifier.create(monoLinea)
			// no onNext because there are no lines with this id after delete
			.expectComplete()
			.verify();
		
		StepVerifier.create(monoConsulta)
			.assertNext(cons -> {
				assertEquals(1, cons.getPropuestaByIndex(0).getCantidadLineaIds());
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	void testAddVariasLineas_And_DeleteVariasLineas_And_DeleteVariasLineasById() {
		lineaService.addVariasLineas(Flux.just(linea4, linea5), propuesta.getId()).blockLast();
		
		StepVerifier.create(lineaService.findById(linea4.getId()))
			.assertNext(line -> {
				assertEquals(linea4.getId(), line.getId());
				assertEquals(2, line.getOrder());
			})
			.expectComplete()
			.verify();
		
		StepVerifier.create(lineaService.findById(linea5.getId()))
			.assertNext(line -> {
				assertEquals(linea5.getId(), line.getId());
				assertEquals(3, line.getOrder());
			})
			.expectComplete()
			.verify();
		
		StepVerifier.create(monoConsulta)
		.assertNext(cons -> {
			assertEquals(3, cons.getPropuestaByIndex(0).getCantidadLineaIds());
			assertTrue(linea4.getId().equals(cons.getPropuestaByIndex(0).getLineaIdByIndex(1)) || linea4.getId().equals(cons.getPropuestaByIndex(0).getLineaIdByIndex(2)));
			assertTrue(linea5.getId().equals(cons.getPropuestaByIndex(0).getLineaIdByIndex(1)) || linea5.getId().equals(cons.getPropuestaByIndex(0).getLineaIdByIndex(2)));
		})
		.expectComplete()
		.verify();
		
		lineaService.deleteVariasLineas(Flux.just(linea4, linea5)).block();
		
		StepVerifier.create(lineaService.findById(linea4.getId()))
		.expectComplete()
		.verify();
	
		StepVerifier.create(lineaService.findById(linea5.getId()))
		.expectComplete()
		.verify();

		StepVerifier.create(monoConsulta).assertNext(cons -> {
			assertEquals(1, cons.getPropuestaByIndex(0).getCantidadLineaIds());
			assertEquals(linea3.getId(), cons.getPropuestaByIndex(0).getLineaIdByIndex(0));
		}).expectComplete().verify();
		
		lineaService.deleteVariasLineasById(Flux.just(linea3.getId())).block();
		
		StepVerifier.create(lineaService.findById(linea3.getId()))
		.expectComplete()
		.verify();
		
		StepVerifier.create(monoConsulta).assertNext(cons -> {
			assertEquals(0, cons.getPropuestaByIndex(0).getCantidadLineaIds());
		}).expectComplete().verify();
	}
	
	@Test
	void testDeleteSeveralLineasFromPropuestaId() {
		lineaService.addVariasLineas(Flux.just(linea4, linea5), propuesta.getId()).blockLast();
		Long deletecount = lineaService.deleteSeveralLineasFromPropuestaId(propuesta.getId()).block();
		
		StepVerifier.create(lineaService.findById(linea3.getId()))
		.expectComplete()
		.verify();
		
		StepVerifier.create(lineaService.findById(linea4.getId()))
		.expectComplete()
		.verify();
	
		StepVerifier.create(lineaService.findById(linea5.getId()))
		.expectComplete()
		.verify();
		
		StepVerifier.create(monoConsulta).assertNext(cons -> {
			assertEquals(0, cons.getPropuestaByIndex(0).getCantidadLineaIds());
			assertEquals(3, deletecount);
		}).expectComplete().verify();
		
	}
	
	@Test
	void testDeleteSeveralLineasFromSeveralPropuestas() {
		lineaService.addVariasLineas(Flux.just(linea4, linea5, linea6, linea7), propuesta.getId()).blockLast();
		
		StepVerifier.create(lineaService.findById(linea3.getId()))
		.assertNext(assertionConsumer -> {})
		.expectComplete()
		.verify();
		
		StepVerifier.create(lineaService.findById(linea4.getId()))
		.assertNext(assertionConsumer -> {})
		.expectComplete()
		.verify();
	
		StepVerifier.create(lineaService.findById(linea5.getId()))
		.assertNext(assertionConsumer -> {})
		.expectComplete()
		.verify();
		
		StepVerifier.create(lineaService.findById(linea6.getId()))
		.assertNext(assertionConsumer -> {})
		.expectComplete()
		.verify();
	
		StepVerifier.create(lineaService.findById(linea7.getId()))
		.assertNext(assertionConsumer -> {})
		.expectComplete()
		.verify();
		
		StepVerifier.create(monoConsulta).assertNext(cons -> {
			assertEquals(3, cons.getPropuestaByIndex(0).getCantidadLineaIds());
			assertEquals(2, cons.getPropuestaByIndex(1).getCantidadLineaIds());
		}).expectComplete().verify();
		
		List<Propuesta> list = new ArrayList<>();
		list.add(propuesta);
		list.add(propuesta2);
		Long deleteCount = lineaService.deleteSeveralLineasFromSeveralPropuestas(list).block();
		
		StepVerifier.create(monoConsulta).assertNext(cons -> {
			assertEquals(0, cons.getPropuestaByIndex(0).getCantidadLineaIds());
			assertEquals(0, cons.getPropuestaByIndex(1).getCantidadLineaIds());
			assertEquals(5, deleteCount);
		}).expectComplete().verify();
		
		StepVerifier.create(lineaService.findById(linea3.getId()))
		.expectComplete()
		.verify();
		
		StepVerifier.create(lineaService.findById(linea4.getId()))
		.expectComplete()
		.verify();
	
		StepVerifier.create(lineaService.findById(linea5.getId()))
		.expectComplete()
		.verify();
		
		StepVerifier.create(lineaService.findById(linea6.getId()))
		.expectComplete()
		.verify();
	
		StepVerifier.create(lineaService.findById(linea7.getId()))
		.expectComplete()
		.verify();
	}

}
