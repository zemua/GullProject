package devs.mrp.gullproject.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.service.PropuestaOperations;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class CustomConsultaRepoImplTest {
	
	ConsultaRepo repo;
	
	@Autowired
	public CustomConsultaRepoImplTest(ConsultaRepo repo) {
		this.repo = repo;
	}
	
	Consulta consulta;
	Propuesta propuesta1;
	PropuestaOperations oper1;
	Propuesta propuesta2;
	PropuestaOperations oper2;
	AtributoForCampo atributo1;
	AtributoForCampo atributo2;
	AtributoForCampo atributo3;
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
	Mono<Consulta> mono;
	
	@BeforeEach
	void inicializacion() {
		
		if (!(repo instanceof CustomConsultaRepo)) {
			fail("ConsultaRepo no extiende CustomConsultaRepo");
		}
		
		repo.deleteAll().block();
		
		atributo1 = new AtributoForCampo();
		atributo1.setId("atributo1id");
		atributo1.setName("atributo1name");
		atributo1.setTipo("atributo1tipo");
		
		atributo2 = new AtributoForCampo();
		atributo2.setId("atributo2id");
		atributo2.setName("atributo2name");
		atributo2.setTipo("atributo2tipo");
		
		atributo3 = new AtributoForCampo();
		atributo3.setId("atributo3id");
		atributo3.setName("atributo3name");
		atributo3.setTipo("atributo3tipo");
		
		
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
		
		propuesta1 = new PropuestaCliente();
		propuesta1.setId("propuesta 1 id");
		propuesta1.setNombre("nombre propuesta 1");
		oper1 = propuesta1.operations();
		oper1.addLineaId(linea1.getId());
		oper1.addLineaId(linea2.getId());
		oper1.addAttribute(atributo1);
		oper1.addAttribute(atributo2);
		
		
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
		
		
		propuesta2 = new PropuestaCliente();
		propuesta2.setId("id propuesta 2");
		propuesta2.setNombre("nombre propuesta 2");
		oper2 = propuesta2.operations();
		oper2.addLineaId(linea3.getId());
		oper2.addLineaId(linea4.getId());
		oper2.addAttribute(atributo2);
		oper2.addAttribute(atributo3);
		
		/**
		 * Arrejuntando todo
		 */
		
		consulta = new Consulta();
		consulta.setId("consulta id");
		consulta.setNombre("consulta nombre");
		consulta.setStatus("estado original");
		consulta.operations().addPropuesta(propuesta1);
		consulta.operations().addPropuesta(propuesta2);
		
		repo.save(consulta).block();
		
		
		
		/**
		 * Assert antes de los tests el estado actual
		 */
		
		
		mono = repo.findById(consulta.getId());		
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals("estado original", cons.getStatus());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals("linea 1 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals("linea 2 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(1));
			assertEquals(atributo1, cons.operations().getPropuestaByIndex(0).getAttributeColumns().get(0));
			assertEquals(atributo2, cons.operations().getPropuestaByIndex(0).getAttributeColumns().get(1));
			assertEquals(2, cons.operations().getPropuestaByIndex(0).getAttributeColumns().size());
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2", cons.operations().getPropuestaByIndex(1).getNombre());
			assertEquals("linea 3 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(0));
			assertEquals("linea 4 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(1));
			assertEquals(atributo2, cons.operations().getPropuestaByIndex(1).getAttributeColumns().get(0));
			assertEquals(atributo3, cons.operations().getPropuestaByIndex(1).getAttributeColumns().get(1));
			assertEquals(2, cons.operations().getPropuestaByIndex(1).getAttributeColumns().size());
		})
		.expectComplete()
		.verify();
	}

	@Test
	void testRemovePropuesta() {
		
		Mono<Consulta> mono = repo.findById(consulta.getId());
		
		StepVerifier.create(mono)
			.assertNext(cons -> {
				assertEquals("consulta nombre", cons.getNombre());
				assertEquals(2, cons.operations().getCantidadPropuestas());
				assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
				assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			})
			.expectComplete()
			.verify();
		
		repo.removePropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(1, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(0));
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
			assertEquals(1, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(0));
		})
		.expectComplete()
		.verify();
		
		repo.addPropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(0));
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(1));
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
			assertEquals(0, cons.operations().getCantidadPropuestas());
		})
		.expectComplete()
		.verify();
		
		repo.addVariasPropuestas(consulta.getId(), Flux.just(propuesta1, propuesta2)).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
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
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
		})
		.expectComplete()
		.verify();
		
		repo.removeVariasPropuestas(consulta.getId(), new Propuesta[] {propuesta1, propuesta2}).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(0, cons.operations().getCantidadPropuestas());
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
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals(2, cons.operations().getPropuestaByIndex(0).operations().getCantidadLineaIds());
		})
		.expectComplete()
		.verify();
		
		propuesta1.setNombre("nombre actualizado");
		oper1.getAllLineaIds().clear();
		assertEquals(0, oper1.getCantidadLineaIds());
		repo.updateNombrePropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.operations().getPropuestaByIndex(0).getId());
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre actualizado", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals(2, cons.operations().getPropuestaByIndex(0).operations().getCantidadLineaIds());
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
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals(2, cons.operations().getPropuestaByIndex(0).operations().getCantidadLineaIds());
			assertEquals(2, cons.operations().getPropuestaByIndex(1).operations().getCantidadLineaIds());
		})
		.expectComplete()
		.verify();
		
		propuesta1.setNombre("nombre actualizado 1");
		oper1.getAllLineaIds().clear();
		assertEquals(0, oper1.getCantidadLineaIds());
		propuesta2.setNombre("nombre actualizado 2");
		oper2.getAllLineaIds().clear();
		assertEquals(0, oper2.getCantidadLineaIds());
		repo.updateNombreVariasPropuestas(consulta.getId(), Flux.just(propuesta1, propuesta2)).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.operations().getPropuestaByIndex(0).getId());
			assertEquals(propuesta2.getId(), cons.operations().getPropuestaByIndex(1).getId());
			assertEquals("nombre actualizado 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals(2, cons.operations().getPropuestaByIndex(0).operations().getCantidadLineaIds());
			assertEquals("nombre actualizado 2", cons.operations().getPropuestaByIndex(1).getNombre());
			assertEquals(2, cons.operations().getPropuestaByIndex(1).operations().getCantidadLineaIds());
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
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals("linea 1 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals("linea 2 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(1));
		})
		.expectComplete()
		.verify();
		
		oper1.getAllLineaIds().set(0, "linea 1 id actualizado");
		oper1.getAllLineaIds().set(1, "linea 2 id actualizado");
		propuesta1.setNombre("nombre propuesta actualizado");
		
		repo.updateLineasDeUnaPropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.operations().getPropuestaByIndex(0).getId());
			assertNotEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals("linea 1 id actualizado", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals("linea 2 id actualizado", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(1));
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
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals("linea 1 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals("linea 2 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(1));
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2", cons.operations().getPropuestaByIndex(1).getNombre());
			assertEquals("linea 3 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(0));
			assertEquals("linea 4 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(1));
		})
		.expectComplete()
		.verify();
		
		oper1.getAllLineaIds().set(0, "linea 1 id actualizado");
		oper1.getAllLineaIds().set(1, "linea 2 id actualizado");
		propuesta1.setNombre("nombre propuesta 1 actualizado");
		
		oper2.getAllLineaIds().set(0, "linea 3 id actualizado");
		oper2.getAllLineaIds().set(1, "linea 4 id actualizado");
		propuesta2.setNombre("nombre propuesta 2 actualizado");
		
		repo.updateLineasDeVariasPropuestas(consulta.getId(), Flux.just(propuesta1, propuesta2)).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.operations().getPropuestaByIndex(0).getId());
			assertNotEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals("linea 1 id actualizado", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals("linea 2 id actualizado", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(1));
			assertEquals(propuesta2.getId(), cons.operations().getPropuestaByIndex(1).getId());
			assertNotEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2", cons.operations().getPropuestaByIndex(1).getNombre());
			assertEquals("linea 3 id actualizado", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(0));
			assertEquals("linea 4 id actualizado", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(1));
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateUnaPropuesta() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		
		oper1.getAllLineaIds().set(0, "id linea 1 actualizado");
		oper1.getAllLineaIds().set(1, "id linea 2 actualizado");
		propuesta1.setNombre("nombre propuesta actualizado");
		
		repo.updateUnaPropuesta(consulta.getId(), propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.operations().getPropuestaByIndex(0).getId());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta actualizado", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals("id linea 1 actualizado", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals("id linea 2 actualizado", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(1));
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateVariasPropuestas() {
		Mono<Consulta> mono = repo.findById(consulta.getId());
		
		oper1.getAllLineaIds().set(0, "linea 1 id actualizado");
		oper1.getAllLineaIds().set(1, "linea 2 id actualizado");
		propuesta1.setNombre("nombre propuesta 1 actualizado");
		
		oper2.getAllLineaIds().set(0, "linea 3 id actualizado");
		oper2.getAllLineaIds().set(1, "linea 4 id actualizado");
		propuesta2.setNombre("nombre propuesta 2 actualizado");
		
		repo.updateVariasPropuestas(consulta.getId(), Flux.just(propuesta1, propuesta2)).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1.getId(), cons.operations().getPropuestaByIndex(0).getId());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1 actualizado", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals("linea 1 id actualizado", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals("linea 2 id actualizado", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(1));
			assertEquals(propuesta2.getId(), cons.operations().getPropuestaByIndex(1).getId());
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2 actualizado", cons.operations().getPropuestaByIndex(1).getNombre());
			assertEquals("linea 3 id actualizado", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(0));
			assertEquals("linea 4 id actualizado", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(1));
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testAddLineaEnPropuesta() {
		Mono<Consulta> mono = repo.findById(consulta.getId());
		
		repo.addLineaEnPropuesta(consulta.getId(), propuesta1.getId(), linea3.getId()).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			oper1.getAllLineaIds().add(linea3.getId());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals(3, cons.operations().getPropuestaByIndex(0).operations().getCantidadLineaIds());
			assertEquals("linea 1 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals("linea 2 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(1));
			assertEquals("linea 3 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(2));
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2", cons.operations().getPropuestaByIndex(1).getNombre());
			assertEquals("linea 3 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(0));
			assertEquals("linea 4 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(1));
		})
		.expectComplete()
		.verify();
		
	}
	
	@Test
	void testRemoveLineaEnPropuesta() {
		Mono<Consulta> mono = repo.findById(consulta.getId());		
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals("linea 1 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals("linea 2 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(1));
			assertEquals(2, cons.operations().getPropuestaByIndex(0).operations().getCantidadLineaIds());
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2", cons.operations().getPropuestaByIndex(1).getNombre());
			assertEquals("linea 3 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(0));
			assertEquals("linea 4 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(1));
		})
		.expectComplete()
		.verify();
		
		repo.removeLineaEnPropuesta(consulta.getId(), propuesta1.getId(), linea1.getId()).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("consulta nombre", cons.getNombre());
			assertEquals(2, cons.operations().getCantidadPropuestas());
			oper1.getAllLineaIds().remove(0);
			assertEquals(propuesta1, cons.operations().getPropuestaByIndex(0));
			assertEquals("nombre propuesta 1", cons.operations().getPropuestaByIndex(0).getNombre());
			assertEquals(1, cons.operations().getPropuestaByIndex(0).operations().getCantidadLineaIds());
			assertEquals("linea 2 id", cons.operations().getPropuestaByIndex(0).operations().getLineaIdByIndex(0));
			assertEquals(propuesta2, cons.operations().getPropuestaByIndex(1));
			assertEquals("nombre propuesta 2", cons.operations().getPropuestaByIndex(1).getNombre());
			assertEquals("linea 3 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(0));
			assertEquals("linea 4 id", cons.operations().getPropuestaByIndex(1).operations().getLineaIdByIndex(1));
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateName() {
		repo.updateName(consulta.getId(), "nombre actualizado").block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("nombre actualizado", cons.getNombre());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateStatus() {
		repo.updateStatus(consulta.getId(), "estado actualizado").block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals("estado actualizado", cons.getStatus());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testFindByPropuestaId() {
		Mono<Consulta> mono = repo.findByPropuestaId(propuesta1.getId());
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals(consulta, cons);
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testAddAttributeToList() {
		repo.addAttributeToList(propuesta1.getId(), atributo3).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals(3, cons.operations().getPropuestaById(propuesta1.getId()).getAttributeColumns().size());
			assertEquals(atributo3, cons.operations().getPropuestaById(propuesta1.getId()).getAttributeColumns().get(2));
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testRemoveAttributeFromList() {
		Mono<Consulta> mono = repo.removeAttributeFromList(propuesta1.getId(), atributo1);
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals(1, cons.operations().getPropuestaById(propuesta1.getId()).getAttributeColumns().size());
			assertEquals(atributo2, cons.operations().getPropuestaById(propuesta1.getId()).getAttributeColumns().get(0));
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateAttributesOfPropuesta() {		
		List<AtributoForCampo> nlist = new ArrayList<>();
		nlist.add(atributo2);
		nlist.add(atributo3);
		
		repo.updateAttributesOfPropuesta(propuesta1.getId(), nlist).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals(2, cons.operations().getPropuestaById(propuesta1.getId()).getAttributeColumns().size());
			assertEquals(atributo2.getId(), cons.operations().getPropuestaById(propuesta1.getId()).getAttributeColumns().get(0).getId());
			assertEquals(atributo3.getId(), cons.operations().getPropuestaById(propuesta1.getId()).getAttributeColumns().get(1).getId());
		})
		.expectComplete()
		.verify();
	}

}
