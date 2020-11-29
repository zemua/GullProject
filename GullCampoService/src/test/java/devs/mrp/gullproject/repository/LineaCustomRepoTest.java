package devs.mrp.gullproject.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LineaCustomRepoTest {
	
	LineaRepo repo;
	
	@Autowired
	public LineaCustomRepoTest(LineaRepo lineaRepo) {
		this.repo = lineaRepo;
		if (!(repo instanceof CustomLineaRepo)) {
			fail("LineaRepo no extiende CustomLineaRepo");
		}
	}

	@Test
	void testAddCampo() {
		
		String id = "linea_id";
		String name = "linea_nombre";
		List<Campo<?>> campos = new ArrayList<>();
		boolean valoresFijos = true;
		Campo<Integer> campo1 = new Campo<>();
		campo1.setId("campo_1_id");
		campo1.setDatos(24702);
		campo1.setAtributoId("atributo_id_1");
		campos.add(campo1);
		Campo<String> campo2 = new Campo<>();
		campo2.setId("campo_2_id");
		campo2.setDatos("datos_en_campo_2");
		campos.add(campo2);
		
		Linea linea = new Linea();
		linea.setCampos(campos);
		linea.setId(id);
		linea.setNombre(name);
		
		repo.save(linea).block();
		
		Mono<Linea> mono = repo.findById(id);
		
		StepVerifier.create(mono)
			.assertNext(line -> {
				assertEquals(name, line.getNombre());
				assertEquals(2, line.getCantidadCampos());
				assertEquals(campo1, line.getCampo(0));
				assertEquals(campo2, line.getCampo(1));
			})
			.expectComplete()
			.verify();
		
		repo.removeCampo(id, campo1).block();
		
		Mono<Linea> mono2 = repo.findById(id);
		
		StepVerifier.create(mono2)
			.assertNext(line -> {
				assertEquals(name, line.getNombre());
				assertEquals(1, line.getCantidadCampos());
				assertEquals(campo2, line.getCampo(0));
			})
			.expectComplete()
			.verify();
		
	}
	
	@Test
	void testRemoveCampo() {
		
		String id = "id_linea";
		String name = "nombre_linea";
		List<Campo<?>> campos = new ArrayList<>();
		Campo<Double> campo1 = new Campo<>();
		campo1.setId("campo_1_id");
		campo1.setAtributoId("campo_1_atributo_id");
		campo1.setDatos(45.67);
		campos.add(campo1);
		Campo<Long> campo2 = new Campo<>();
		campo2.setAtributoId("campo_2_atributo_id");
		campo2.setDatos(562398L);
		campo2.setId("campo_2_id");
		// el 2 se añade desde mongo
		
		Linea linea = new Linea();
		linea.setCampos(campos);
		linea.setId(id);
		linea.setNombre(name);
		
		repo.save(linea).block();
		
		Mono<Linea> mono = repo.findById(id);
		
		StepVerifier.create(mono)
			.assertNext(line -> {
				assertEquals(name, line.getNombre());
				assertEquals(1, line.getCantidadCampos());
				assertEquals(campo1, line.getCampo(0));
			})
			.expectComplete()
			.verify();
		
		repo.addCampo(id, campo2).block();
		
		Mono<Linea> mono2 = repo.findById(id);
		
		StepVerifier.create(mono2)
			.assertNext(line -> {
				assertEquals(name, line.getNombre());
				assertEquals(2, line.getCantidadCampos());
				assertEquals(campo1, line.getCampo(0));
				assertEquals(campo2, line.getCampo(1));
			})
			.expectComplete()
			.verify();
		
		// comprobar que no hace duplicados al ser "add to set" en lugar de "push"
		repo.addCampo(id, campo2).block();
		
		Mono<Linea> mono3 = repo.findById(id);
		
		StepVerifier.create(mono3)
			.assertNext(line -> {
				assertEquals(name, line.getNombre());
				assertEquals(2, line.getCantidadCampos());
				assertEquals(campo1, line.getCampo(0));
				assertEquals(campo2, line.getCampo(1));
			})
			.expectComplete()
			.verify();
		
	}

}
