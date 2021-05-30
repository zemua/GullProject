package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.repositorios.AtributoRepo;
import devs.mrp.gullproject.repositorios.CustomAtributoRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AtributoServiceTest {

	AtributoService atributoService;
	AtributoRepo atributoRepo;
	
	@Autowired
	public AtributoServiceTest(AtributoService atributoService, AtributoRepo atributoRepo) {
		this.atributoService = atributoService;
		this.atributoRepo = atributoRepo;
		if (!(atributoRepo instanceof CustomAtributoRepo)) {
			fail("atributoRepo does not extend CustomAtributoRepo");
		}
	}
	
	Atributo at1;
	Atributo at2;
	Atributo at3;
	
	@BeforeEach
	void setup() {
		atributoRepo.deleteAll().block();
		
		at1 = new Atributo();
		at1.setName("nombre1");
		at1.setTipo(DataFormat.DESCRIPCION);
		at2 = new Atributo();
		at2.setName("nombre2");
		at2.setTipo(DataFormat.DESCRIPCION);
		at3 = new Atributo();
		at3.setName("nombre3");
		at3.setTipo(DataFormat.DESCRIPCION);
	}
	
	@Test
	void testSave() {
		atributoService.save(at1).block();
		Mono<Atributo> mono = atributoService.findById(at1.getId());
		StepVerifier.create(mono)
		.assertNext(at -> {
			assertEquals(1, at.getOrden());
		})
		.expectComplete()
		.verify();
		
		atributoService.save(at2).block();
		mono = atributoService.findById(at2.getId());
		StepVerifier.create(mono)
		.assertNext(at -> {
			assertEquals(2, at.getOrden());
		})
		.expectComplete()
		.verify();
		
		atributoService.save(at3).block();
		mono = atributoService.findById(at3.getId());
		StepVerifier.create(mono)
		.assertNext(at -> {
			assertEquals(3, at.getOrden());
		})
		.expectComplete()
		.verify();
	}
	
}
