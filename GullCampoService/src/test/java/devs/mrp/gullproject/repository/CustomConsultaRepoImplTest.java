package devs.mrp.gullproject.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.SolicitudCliente;
import devs.mrp.gullproject.service.AtributoServiceProxy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CustomConsultaRepoImplTest {
	
	ConsultaRepo repo;
	
	@MockBean
	AtributoServiceProxy atributoServiceProxy;
	
	@Autowired
	public CustomConsultaRepoImplTest(ConsultaRepo repo) {
		this.repo = repo;
		if (!(repo instanceof CustomConsultaRepo)) {
			fail("ConsultaRepo no extiende CustomConsultaRepo");
		}
	}
	
	Consulta consulta;
	Propuesta propuesta1;
	Propuesta propuesta2;
	Linea linea1;
	Linea linea2;
	Linea linea3;
	Linea linea4;
	Campo<String> campo1;
	Campo<String> campo2;
	Campo<String> campo3;
	Campo<String> campo4;
	Campo<String> campo5;
	Campo<String> campo6;
	Campo<String> campo7;
	Campo<String> campo8;
	
	@BeforeEach
	void inicializacion() {
		
		repo.deleteAll().block();
		
		
		List<Campo<?>> campos1 = new ArrayList<>();
		campo1 = new Campo<>();
		campo1.setId("campo_1_id");
		campo1.setAtributoId("atributo_id_1");
		campo1.setDatos("datos campo 1");
		campos1.add(campo1);
		campo2 = new Campo<>();
		campo2.setAtributoId("atributo_id_2");
		campo2.setDatos("datos campo 2");
		campo2.setId("campo_2_id");
		campos1.add(campo2);
		
		linea1 = new Linea();
		linea1.setCampos(campos1);
		linea1.setId("linea 1 id");
		linea1.setNombre("nombre linea 1");
		
		
		
		List<Campo<?>> campos2 = new ArrayList<>();
		campo3 = new Campo<>();
		campo3.setId("campo_3_id");
		campo3.setAtributoId("atributo_id_1");
		campo3.setDatos("datos campo 3");
		campos2.add(campo3);
		campo4 = new Campo<>();
		campo4.setAtributoId("atributo_id_2");
		campo4.setDatos("datos campo 4");
		campo4.setId("campo_4_id");
		campos2.add(campo4);
		
		linea2 = new Linea();
		linea2.setCampos(campos2);
		linea2.setId("linea 2 id");
		linea2.setNombre("nombre linea 2");
		
		
		propuesta1 = new SolicitudCliente();
		propuesta1.setId("propuesta 1 id");
		propuesta1.setNombre("nombre propuesta 1");
		propuesta1.addLinea(linea1);
		propuesta1.addLinea(linea2);
		
		
		/**
		 * Segunda propuesta
		 */
		
		List<Campo<?>> campos3 = new ArrayList<>();
		campo5 = new Campo<>();
		campo5.setId("campo_5_id");
		campo5.setAtributoId("atributo id 1");
		campo5.setDatos("datos campo 5");
		campos3.add(campo5);
		campo6 = new Campo<>();
		campo6.setAtributoId("atributo id 2");
		campo6.setDatos("datos campo 6");
		campo6.setId("campo_6_id");
		campos3.add(campo6);
		
		linea3 = new Linea();
		linea3.setCampos(campos3);
		linea3.setId("linea 3 id");
		linea3.setNombre("nombre linea 3");
		
		
		
		List<Campo<?>> campos4 = new ArrayList<>();
		campo7 = new Campo<>();
		campo7.setId("campo_7_id");
		campo7.setAtributoId("atributo id 1");
		campo7.setDatos("datos campo 7");
		campos4.add(campo7);
		campo8 = new Campo<>();
		campo8.setAtributoId("atributo id 2");
		campo8.setDatos("datos campo 8");
		campo8.setId("campo_8_id");
		campos4.add(campo8);
		
		linea4 = new Linea();
		linea4.setCampos(campos4);
		linea4.setId("linea 4 id");
		linea4.setNombre("nombre linea 4");
		
		
		propuesta2 = new SolicitudCliente();
		propuesta2.setId("id propuesta 2");
		propuesta2.setNombre("nombre propuesta 2");
		propuesta2.addLinea(linea3);
		propuesta2.addLinea(linea4);
		
		/**
		 * Arrejuntando todo
		 */
		
		consulta = new Consulta();
		consulta.setId("consulta id");
		consulta.setNombre("consulta nombre");
		consulta.addPropuesta(propuesta1);
		consulta.addPropuesta(propuesta2);
		
		repo.save(consulta).block();
	}

	@Test
	void testRemovePropuesta() {
		
		Mono<Consulta> mono = repo.findById(consulta.getId());
		
		StepVerifier.create(mono)
			.assertNext(cons -> {
				assertEquals("consulta nombre", cons.getNombre());
				assertEquals(2, cons.getCantidadPropuestas());
				assertEquals(propuesta1, cons.getPropuestaByIndex(0));
				assertEquals(propuesta2, cons.getPropuestaByIndex(1));
			})
			.expectComplete()
			.verify();
		
		repo.removePropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(1, cons.getCantidadPropuestas());
			assertEquals(propuesta2, cons.getPropuestaByIndex(0));
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testAddPropuesta() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		repo.removePropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(1, cons.getCantidadPropuestas());
			assertEquals(propuesta2, cons.getPropuestaByIndex(0));
		})
		.expectComplete()
		.verify();
		
		repo.addPropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta2, cons.getPropuestaByIndex(0));
			assertEquals(propuesta1, cons.getPropuestaByIndex(1));
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testAddVariasPropuestas() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		repo.removePropuesta(consulta.getId(), propuesta1).block();
		repo.removePropuesta(consulta.getId(), propuesta2).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(0, cons.getCantidadPropuestas());
		})
		.expectComplete()
		.verify();
		
		repo.addVariasPropuestas(consulta.getId(), Flux.just(propuesta1, propuesta2)).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals(propuesta2, cons.getPropuestaByIndex(1));
		})
		.expectComplete()
		.verify();
		
	}
	
	@Test
	void testRemoveVariasPropuestas() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
				
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals(propuesta2, cons.getPropuestaByIndex(1));
		})
		.expectComplete()
		.verify();
		
		repo.removeVariasPropuestas(consulta.getId(), new Propuesta[] {propuesta1, propuesta2}).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(0, cons.getCantidadPropuestas());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateNombrePropuesta() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals(propuesta2, cons.getPropuestaByIndex(1));
			assertEquals("nombre propuesta 1", cons.getPropuestaByIndex(0).getNombre());
			assertEquals(2, cons.getPropuestaByIndex(0).getCantidadLineas());
		})
		.expectComplete()
		.verify();
		
		propuesta1.setNombre("nombre actualizado");
		propuesta1.getAllLineas().clear();
		assertEquals(0, propuesta1.getCantidadLineas());
		repo.updateNombrePropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.getPropuestaByIndex(0).getId());
			assertEquals(propuesta2, cons.getPropuestaByIndex(1));
			assertEquals("nombre actualizado", cons.getPropuestaByIndex(0).getNombre());
			assertEquals(2, cons.getPropuestaByIndex(0).getCantidadLineas());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateNombreVariasPropuestas() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals(propuesta2, cons.getPropuestaByIndex(1));
			assertEquals("nombre propuesta 1", cons.getPropuestaByIndex(0).getNombre());
			assertEquals(2, cons.getPropuestaByIndex(0).getCantidadLineas());
			assertEquals(2, cons.getPropuestaByIndex(1).getCantidadLineas());
		})
		.expectComplete()
		.verify();
		
		propuesta1.setNombre("nombre actualizado 1");
		propuesta1.getAllLineas().clear();
		assertEquals(0, propuesta1.getCantidadLineas());
		propuesta2.setNombre("nombre actualizado 2");
		propuesta2.getAllLineas().clear();
		assertEquals(0, propuesta2.getCantidadLineas());
		repo.updateNombreVariasPropuestas(consulta.getId(), Flux.just(propuesta1, propuesta2)).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.getPropuestaByIndex(0).getId());
			assertEquals(propuesta2.getId(), cons.getPropuestaByIndex(1).getId());
			assertEquals("nombre actualizado 1", cons.getPropuestaByIndex(0).getNombre());
			assertEquals(2, cons.getPropuestaByIndex(0).getCantidadLineas());
			assertEquals("nombre actualizado 2", cons.getPropuestaByIndex(1).getNombre());
			assertEquals(2, cons.getPropuestaByIndex(1).getCantidadLineas());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateLineasDeUnaPropuesta() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.getPropuestaByIndex(0).getNombre());
			assertEquals("nombre linea 1", cons.getPropuestaByIndex(0).getLinea(0).getNombre());
			assertEquals("nombre linea 2", cons.getPropuestaByIndex(0).getLinea(1).getNombre());
		})
		.expectComplete()
		.verify();
		
		propuesta1.getLinea(0).setNombre("nombre linea 1 actualizado");
		propuesta1.getLinea(1).setNombre("nombre linea 2 actualizado");
		propuesta1.setNombre("nombre propuesta actualizado");
		
		repo.updateLineasDeUnaPropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.getPropuestaByIndex(0).getId());
			assertNotEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.getPropuestaByIndex(0).getNombre());
			assertEquals("nombre linea 1 actualizado", cons.getPropuestaByIndex(0).getLinea(0).getNombre());
			assertEquals("nombre linea 2 actualizado", cons.getPropuestaByIndex(0).getLinea(1).getNombre());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateLineasDeVariasPropuestas() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.getPropuestaByIndex(0).getNombre());
			assertEquals("nombre linea 1", cons.getPropuestaByIndex(0).getLinea(0).getNombre());
			assertEquals("nombre linea 2", cons.getPropuestaByIndex(0).getLinea(1).getNombre());
			assertEquals(propuesta2, cons.getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2", cons.getPropuestaByIndex(1).getNombre());
			assertEquals("nombre linea 3", cons.getPropuestaByIndex(1).getLinea(0).getNombre());
			assertEquals("nombre linea 4", cons.getPropuestaByIndex(1).getLinea(1).getNombre());
		})
		.expectComplete()
		.verify();
		
		propuesta1.getLinea(0).setNombre("nombre linea 1 actualizado");
		propuesta1.getLinea(1).setNombre("nombre linea 2 actualizado");
		propuesta1.setNombre("nombre propuesta 1 actualizado");
		
		propuesta2.getLinea(0).setNombre("nombre linea 3 actualizado");
		propuesta2.getLinea(1).setNombre("nombre linea 4 actualizado");
		propuesta2.setNombre("nombre propuesta 2 actualizado");
		
		repo.updateLineasDeVariasPropuestas(consulta.getId(), Flux.just(propuesta1, propuesta2)).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.getPropuestaByIndex(0).getId());
			assertNotEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.getPropuestaByIndex(0).getNombre());
			assertEquals("nombre linea 1 actualizado", cons.getPropuestaByIndex(0).getLinea(0).getNombre());
			assertEquals("nombre linea 2 actualizado", cons.getPropuestaByIndex(0).getLinea(1).getNombre());
			assertEquals(propuesta2.getId(), cons.getPropuestaByIndex(1).getId());
			assertNotEquals(propuesta2, cons.getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2", cons.getPropuestaByIndex(1).getNombre());
			assertEquals("nombre linea 3 actualizado", cons.getPropuestaByIndex(1).getLinea(0).getNombre());
			assertEquals("nombre linea 4 actualizado", cons.getPropuestaByIndex(1).getLinea(1).getNombre());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateUnaPropuesta() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.getPropuestaByIndex(0).getNombre());
			assertEquals("nombre linea 1", cons.getPropuestaByIndex(0).getLinea(0).getNombre());
			assertEquals("nombre linea 2", cons.getPropuestaByIndex(0).getLinea(1).getNombre());
		})
		.expectComplete()
		.verify();
		
		propuesta1.getLinea(0).setNombre("nombre linea 1 actualizado");
		propuesta1.getLinea(1).setNombre("nombre linea 2 actualizado");
		propuesta1.setNombre("nombre propuesta actualizado");
		
		repo.updateUnaPropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.getPropuestaByIndex(0).getId());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals("nombre propuesta actualizado", cons.getPropuestaByIndex(0).getNombre());
			assertEquals("nombre linea 1 actualizado", cons.getPropuestaByIndex(0).getLinea(0).getNombre());
			assertEquals("nombre linea 2 actualizado", cons.getPropuestaByIndex(0).getLinea(1).getNombre());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateVariasPropuestas() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.getPropuestaByIndex(0).getNombre());
			assertEquals("nombre linea 1", cons.getPropuestaByIndex(0).getLinea(0).getNombre());
			assertEquals("nombre linea 2", cons.getPropuestaByIndex(0).getLinea(1).getNombre());
			assertEquals(propuesta2, cons.getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2", cons.getPropuestaByIndex(1).getNombre());
			assertEquals("nombre linea 3", cons.getPropuestaByIndex(1).getLinea(0).getNombre());
			assertEquals("nombre linea 4", cons.getPropuestaByIndex(1).getLinea(1).getNombre());
		})
		.expectComplete()
		.verify();
		
		propuesta1.getLinea(0).setNombre("nombre linea 1 actualizado");
		propuesta1.getLinea(1).setNombre("nombre linea 2 actualizado");
		propuesta1.setNombre("nombre propuesta 1 actualizado");
		
		propuesta2.getLinea(0).setNombre("nombre linea 3 actualizado");
		propuesta2.getLinea(1).setNombre("nombre linea 4 actualizado");
		propuesta2.setNombre("nombre propuesta 2 actualizado");
		
		repo.updateVariasPropuestas(consulta.getId(), Flux.just(propuesta1, propuesta2)).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.getPropuestaByIndex(0).getId());
			assertEquals(propuesta1, cons.getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1 actualizado", cons.getPropuestaByIndex(0).getNombre());
			assertEquals("nombre linea 1 actualizado", cons.getPropuestaByIndex(0).getLinea(0).getNombre());
			assertEquals("nombre linea 2 actualizado", cons.getPropuestaByIndex(0).getLinea(1).getNombre());
			assertEquals(propuesta2.getId(), cons.getPropuestaByIndex(1).getId());
			assertEquals(propuesta2, cons.getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2 actualizado", cons.getPropuestaByIndex(1).getNombre());
			assertEquals("nombre linea 3 actualizado", cons.getPropuestaByIndex(1).getLinea(0).getNombre());
			assertEquals("nombre linea 4 actualizado", cons.getPropuestaByIndex(1).getLinea(1).getNombre());
		})
		.expectComplete()
		.verify();
	}

}
