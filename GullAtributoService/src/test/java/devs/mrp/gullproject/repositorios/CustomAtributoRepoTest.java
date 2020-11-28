package devs.mrp.gullproject.repositorios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.domains.Tipo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CustomAtributoRepoTest {

	AtributoRepo repo;
	
	@Autowired
	public CustomAtributoRepoTest(AtributoRepo atributoRepo) {
		this.repo = atributoRepo;
		if (!(repo instanceof CustomAtributoRepo)) {
			fail("AtributoRepo no extiende CustomAtributoRepo");
		}
	}
	
	@Test
	void testRemoveAtributo() {
		
		String id = "attid";
		String name = "att name";
		List<Tipo> tipos = new ArrayList<>();
		boolean valoresFijos = true;
		Tipo tipo1 = new Tipo();
		tipo1.setNombre("tipo1");
		tipo1.setDataFormat(DataFormat.atributoTexto);
		Tipo tipo2 = new Tipo();
		tipo2.setNombre("tipo2");
		tipo2.setDataFormat(DataFormat.cantidad);
		
		Atributo atributo = new Atributo();
		atributo.setId(id);
		atributo.setName(name);
		atributo.addTipo(tipo1);
		atributo.addTipo(tipo2);
		atributo.setValoresFijos(valoresFijos);
		
		repo.save(atributo).block();
		
		Mono<Atributo> mono = repo.findById(id);
		
		StepVerifier.create(mono)
			.assertNext(attr -> {
				assertEquals(name, attr.getName());
				assertEquals(2, attr.getCantidadTipos());
				assertEquals(tipo1, attr.getTipo(0));
				assertEquals(tipo2, attr.getTipo(1));
			})
			.expectComplete()
			.verify();
		
		repo.removeAtributo(id, tipo1).block();
		
		Mono<Atributo> mono2 = repo.findById(id);
		
		StepVerifier.create(mono2)
			.assertNext(attr -> {
				assertEquals(name, attr.getName());
				assertEquals(1, attr.getCantidadTipos());
				assertEquals(tipo2, attr.getTipo(0));
			})
			.expectComplete()
			.verify();
		
	}

	@Test
	void testAddAtributo() {
		
		String id = "attid";
		String name = "att name";
		List<Tipo> tipos = new ArrayList<>();
		boolean valoresFijos = true;
		Tipo tipo1 = new Tipo();
		tipo1.setNombre("tipo1");
		tipo1.setDataFormat(DataFormat.atributoTexto);
		Tipo tipo2 = new Tipo();
		tipo2.setNombre("tipo2");
		tipo2.setDataFormat(DataFormat.cantidad);
		
		Atributo atributo = new Atributo();
		atributo.setId(id);
		atributo.setName(name);
		atributo.addTipo(tipo1);
		atributo.setValoresFijos(valoresFijos);
		
		repo.save(atributo).block();
		
		Mono<Atributo> mono = repo.findById(id);
		
		StepVerifier.create(mono)
			.assertNext(attr -> {
				assertEquals(name, attr.getName());
				assertEquals(1, attr.getCantidadTipos());
				assertEquals(tipo1, attr.getTipo(0));
			})
			.expectComplete()
			.verify();
		
		repo.addAtributo(id, tipo2).block();
		
		Mono<Atributo> mono2 = repo.findById(id);
		
		StepVerifier.create(mono2)
			.assertNext(attr -> {
				assertEquals(name, attr.getName());
				assertEquals(2, attr.getCantidadTipos());
				assertEquals(tipo1, attr.getTipo(0));
				assertEquals(tipo2, attr.getTipo(1));
			})
			.expectComplete()
			.verify();
		
		// comprobar que no hace duplicados al ser "add to set" en lugar de "push"
		repo.addAtributo(id, tipo2).block();
		
		Mono<Atributo> mono3 = repo.findById(id);
		
		StepVerifier.create(mono3)
			.assertNext(attr -> {
				assertEquals(name, attr.getName());
				assertEquals(2, attr.getCantidadTipos());
				assertEquals(tipo1, attr.getTipo(0));
				assertEquals(tipo2, attr.getTipo(1));
			})
			.expectComplete()
			.verify();
		
	}

}
