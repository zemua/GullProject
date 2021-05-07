package devs.mrp.gullproject.repositorios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CustomAtributoRepoTest {

	AtributoRepo repo;
	
	String id;
	String name;
	DataFormat tipo;
	boolean valoresFijos;
	Atributo atributo;
	Mono<Atributo> mono;
	
	@Autowired
	public CustomAtributoRepoTest(AtributoRepo atributoRepo) {
		this.repo = atributoRepo;
		if (!(repo instanceof CustomAtributoRepo)) {
			fail("AtributoRepo no extiende CustomAtributoRepo");
		}
	}
	
	@BeforeEach
	void initialization() {
		id = "attid";
		name = "att name";
		tipo = DataFormat.DESCRIPCION;
		valoresFijos = true;
		
		atributo = new Atributo();
		atributo.setId(id);
		atributo.setName(name);
		atributo.setTipo(tipo);
		atributo.setValoresFijos(valoresFijos);
		
		repo.save(atributo).block();
		
		mono = repo.findById(id);
		
		StepVerifier.create(mono)
			.assertNext(attr -> {
				assertEquals(name, attr.getName());
				assertEquals(id, attr.getId());
				assertEquals(tipo, attr.getTipo());
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	void testUpdateNameOfAtributo() {
		
		repo.updateNameOfAtributo(id, "updatedName").block();
		
		StepVerifier.create(mono)
			.assertNext(attr -> {
				assertEquals("updatedName", attr.getName());
				assertEquals(DataFormat.DESCRIPCION, attr.getTipo());
			})
			.expectComplete()
			.verify();
		
	}
}
