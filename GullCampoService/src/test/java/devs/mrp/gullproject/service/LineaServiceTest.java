package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import devs.mrp.gullproject.domains.Linea;
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
	
	@MockBean
	CampoService campoService;
	
	@Autowired
	public LineaServiceTest(LineaService lineaService, LineaRepo lineaRepo) {
		this.lineaService = lineaService;
		this.lineaRepo = lineaRepo;
		if (!(lineaRepo instanceof CustomLineaRepo)) {
			fail("LineaRepo no extiende CustomLineaRepo");
		}
	}
	
	Campo<Integer> campo1;
	Campo<String> campo2;
	Campo<String> campo3;
	Linea linea1;
	Linea linea2;
	Mono<Linea> mono;
	Flux<Linea> flux;
	
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
		.expectComplete()
		.verify();
	}

}
