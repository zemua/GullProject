package devs.mrp.gullproject.repositorios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.domains.Tipo;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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
		atributo.getTipos().add(tipo1);
		atributo.getTipos().add(tipo2);
		
		repo.save(atributo).block();
		
		Flux<Material> flux = materialRepo.findAllByName(name);
		
		StepVerifier.create(flux)
			.assertNext(material -> {
				assertEquals(name, material.getName());
				assertNotNull(material.getId());
				
			})
			.expectComplete()
			.verify();
		
		fail("Not yet implemented");
	}

	@Test
	void testRemoveAtributo() {
		fail("Not yet implemented");
	}

}
