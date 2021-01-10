package devs.mrp.gullproject.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.service.AtributoServiceProxy;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CustomPropuestaRepoImplTest {
	
	PropuestaRepo propuestaRepo;
	
	@MockBean
	AtributoServiceProxy atributoServiceProxy;
	
	@Autowired
	public CustomPropuestaRepoImplTest(PropuestaRepo propuestaRepo) {
		this.propuestaRepo = propuestaRepo;
		if (!(propuestaRepo instanceof CustomPropuestaRepo)) {
			fail("PropuestaRepo no extiende CustomPropuestaRepo");
		}
	}
	
	Propuesta propuesta;
	Linea linea1;
	Linea linea2;
	
	@BeforeEach
	void inicializacion() {
		
		propuestaRepo.deleteAll().block();
		
		propuesta = new PropuestaCliente();
		propuesta.setId(new ObjectId().toString());
		propuesta.setNombre("nombre propuesta");
		
		
		
		List<Campo<?>> campos1 = new ArrayList<>();
		Campo<Double> campo1 = new Campo<>();
		campo1.setId("campo_1_id");
		campo1.setAtributoId("campo_1_atributo_id");
		campo1.setDatos(45.67);
		campos1.add(campo1);
		Campo<Long> campo2 = new Campo<>();
		campo2.setAtributoId("campo_2_atributo_id");
		campo2.setDatos(562398L);
		campo2.setId("campo_2_id");
		campos1.add(campo2);
		
		linea1 = new Linea();
		linea1.setCampos(campos1);
		linea1.setId(new ObjectId().toString());
		linea1.setNombre("nombre linea 1");
		
		
		
		List<Campo<?>> campos2 = new ArrayList<>();
		Campo<Double> campo3 = new Campo<>();
		campo3.setId("campo_1_id");
		campo3.setAtributoId("campo_1_atributo_id");
		campo3.setDatos(45.67);
		campos2.add(campo3);
		Campo<Long> campo4 = new Campo<>();
		campo4.setAtributoId("campo_2_atributo_id");
		campo4.setDatos(562398L);
		campo4.setId("campo_2_id");
		campos2.add(campo4);
		
		linea2 = new Linea();
		linea2.setCampos(campos2);
		linea2.setId(new ObjectId().toString());
		linea2.setNombre("nombre linea 2");
		
		
		
		propuesta.addLineaId(linea1.getId());
		
		propuestaRepo.save(propuesta).block();
	}

	@Test
	void testAddLineaAndRemoveLinea() throws Exception {
		
		Mono<Propuesta> mono = propuestaRepo.findById(propuesta.getId());
		
		StepVerifier.create(mono)
			.assertNext(prop -> {
				assertEquals("nombre propuesta", prop.getNombre());
				assertEquals(1, prop.getCantidadLineaIds());
				assertEquals(linea1.getId(), prop.getLineaIdByIndex(0));
			})
			.expectComplete()
			.verify();
		
		propuestaRepo.addLinea(propuesta.getId(), linea2.getId()).block();
		
		StepVerifier.create(mono)
		.assertNext(prop -> {
			assertEquals("nombre propuesta", prop.getNombre());
			assertEquals(2, prop.getCantidadLineaIds());
			assertEquals(linea1.getId(), prop.getLineaIdByIndex(0));
			assertEquals(linea2.getId(), prop.getLineaIdByIndex(1));
		})
		.expectComplete()
		.verify();
		
		propuestaRepo.removeLinea(propuesta.getId(), linea1.getId()).block();
		
		StepVerifier.create(mono)
		.assertNext(prop -> {
			assertEquals("nombre propuesta", prop.getNombre());
			assertEquals(1, prop.getCantidadLineaIds());
			assertEquals(linea2.getId(), prop.getLineaIdByIndex(0));
		})
		.expectComplete()
		.verify();
		
	}

}
