package devs.mrp.gullproject.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.service.linea.LineaOperations;
import reactor.core.publisher.Flux;
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
	
	Campo<Integer> campo1;
	Campo<String> campo2;
	Campo<String> campo3;
	Linea linea;
	LineaOperations lineaO;
	Mono<Linea> mono;
	Flux<Linea> flux;
	
	Linea l1;
	LineaOperations l1o;
	Linea l2;
	LineaOperations l2o;
	Linea l3;
	LineaOperations l3o;
	Linea l4;
	LineaOperations l4o;
	Linea l5;
	LineaOperations l5o;
	Linea l6;
	LineaOperations l6o;
	Linea l7;
	LineaOperations l7o;
	Linea l8;
	LineaOperations l8o;
	Linea l9;
	LineaOperations l9o;
	
	@BeforeEach
	void setUp() {
		repo.deleteAll().block();
		
		String id = "linea_id";
		String name = "linea_nombre";
		List<Campo<?>> campos = new ArrayList<>();
		boolean valoresFijos = true;
		
		campo1 = new Campo<>();
		campo1.setId("campo_1_id");
		campo1.setDatos(24702);
		campo1.setAtributoId("atributo_id_1");
		campos.add(campo1);
		
		campo2 = new Campo<>();
		campo2.setId("campo_2_id");
		campo2.setDatos("datos_en_campo_2");
		campo2.setAtributoId("atributo_id_2");
		campos.add(campo2);
		
		campo3 = new Campo<>();
		campo3.setId("campo_3_id");
		campo3.setDatos("datos_en_campo_3");
		campo3.setAtributoId("atributo_id_3");
		campos.add(campo3);
		
		linea = new Linea();
		lineaO = new LineaOperations(linea);
		linea.setCampos(campos);
		linea.setPropuestaId("propA");
		linea.setId(id);
		linea.setNombre(name);
		linea.setOrder(2);
		linea.setParentId("parent id");
		List<String> counter = new ArrayList<>();
		counter.add("counter line id");
		linea.setCounterLineId(counter);
		
		repo.save(linea).block();
		
		mono = repo.findById(id);
		
		l1 = new Linea();
		l1.setPropuestaId("p1");
		l1o = new LineaOperations(l1);
		l2 = new Linea();
		l2.setPropuestaId("p1");
		l2o = new LineaOperations(l2);
		l3 = new Linea();
		l3.setPropuestaId("p2");
		l3o = new LineaOperations(l3);
		l4 = new Linea();
		l4.setPropuestaId("p2");
		l4o = new LineaOperations(l4);
		l5 = new Linea();
		l5.setPropuestaId("p3");
		l5o = new LineaOperations(l5);
		l6 = new Linea();
		l6.setPropuestaId("p3");
		l6o = new LineaOperations(l6);
		l7 = new Linea();
		l7.setPropuestaId("p4");
		l7o = new LineaOperations(l7);
		l8 = new Linea();
		l8.setPropuestaId("p4");
		l8o = new LineaOperations(l8);
		l9 = new Linea();
		l9.setPropuestaId("p5");
		l9o = new LineaOperations(l9);
		
		List<Linea> list = Arrays.asList(l1, l2, l3, l4, l5, l6, l7, l8, l9);
		repo.saveAll(list).blockLast();
		
		flux = repo.findAll();
	}
	
	@AfterEach
	void clear() {
		repo.deleteAll().block();
	}

	@Test
	void testRemoveCampo() {
		
		repo.deleteAll().block();
		
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
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(2, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
				assertEquals(campo2, lineo.getCampoByIndex(1));
			})
			.expectComplete()
			.verify();
		
		repo.removeCampo(id, campo1).block();
		
		Mono<Linea> mono2 = repo.findById(id);
		
		StepVerifier.create(mono2)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(1, lineo.getCantidadCampos());
				assertEquals(campo2, lineo.getCampoByIndex(0));
			})
			.expectComplete()
			.verify();
		
	}
	
	@Test
	void testAddCampo() {
		
		repo.deleteAll().block();
		
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
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(1, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
			})
			.expectComplete()
			.verify();
		
		repo.addCampo(id, campo2).block();
		
		Mono<Linea> mono2 = repo.findById(id);
		
		StepVerifier.create(mono2)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(2, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
				assertEquals(campo2, lineo.getCampoByIndex(1));
			})
			.expectComplete()
			.verify();
		
		// comprobar que no hace duplicados al ser "add to set" en lugar de "push"
		repo.addCampo(id, campo2).block();
		
		Mono<Linea> mono3 = repo.findById(id);
		
		StepVerifier.create(mono3)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(2, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
				assertEquals(campo2, lineo.getCampoByIndex(1));
			})
			.expectComplete()
			.verify();
		
	}
	
	@Test
	void testUpdateCampo() {
		
		repo.deleteAll().block();
		
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
		campos.add(campo2);
		
		Linea linea = new Linea();
		linea.setCampos(campos);
		linea.setId(id);
		linea.setNombre(name);
		
		repo.save(linea).block();
		
		/**
		 * Verificar inicialización con 2 campos
		 */
		
		Mono<Linea> mono = repo.findById(id);
		
		StepVerifier.create(mono)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(2, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
				assertEquals(campo2, lineo.getCampoByIndex(1));
			})
			.expectComplete()
			.verify();
		
		/**
		 * Cuando se actualiza un campo que existe
		 * comprobar que se actualizan los datos de ese campo
		 */
		
		Campo<Long> campo3 = new Campo();
		campo3.setDatos(5896L);
		campo3.setId(campo2.getId());
		campo3.setAtributoId(campo2.getAtributoId());
		
		repo.updateCampo(id, campo3).block();
		
		//Mono<Linea> mono2 = repo.findById(id);
		
		StepVerifier.create(mono)
		.assertNext(line -> {
			LineaOperations lineo = new LineaOperations(line);
			assertEquals(name, line.getNombre());
			assertEquals(2, lineo.getCantidadCampos());
			assertEquals(campo1, lineo.getCampoByIndex(0));
			assertEquals(campo3, lineo.getCampoByIndex(1));
			assertNotEquals(campo2, lineo.getCampoByIndex(1));
		})
		.expectComplete()
		.verify();
		
		/**
		 * Cuando se actualiza un campo que no existe
		 * entonces no se modifica la db
		 */
		
		Campo<String> campo4 = new Campo();
		campo4.setDatos("datos_del_campo");
		campo4.setId("campo_4_id");
		campo4.setAtributoId("campo_4_atributo_id");
		
		repo.updateCampo(id, campo4).block();
		
		//Mono<Linea> mono3 = repo.findById(id);
		
		StepVerifier.create(mono)
		.assertNext(line -> {
			LineaOperations lineo = new LineaOperations(line);
			assertEquals(name, line.getNombre());
			assertEquals(2, lineo.getCantidadCampos());
			assertEquals(campo1, lineo.getCampoByIndex(0));
			assertEquals(campo3, lineo.getCampoByIndex(1));
			assertNotEquals(campo2, lineo.getCampoByIndex(1));
		})
		.expectComplete()
		.verify();
		
	}
	
	@Test
	public void deleteByIdReturningDeletedCount() {
		
		repo.deleteAll().block();
		
		Linea linea1 = new Linea();
		linea1.setCampos(new ArrayList<Campo<?>>());
		linea1.setId("id_linea_1");
		linea1.setNombre("nombre_linea_1");
		
		repo.save(linea1).block();
		
		Linea linea2 = new Linea();
		linea2.setCampos(new ArrayList<Campo<?>>());
		linea2.setId("id_linea_2");
		linea2.setNombre("nombre_linea_2");
		
		repo.save(linea2).block();
		
		/**
		 * Verificar inicialización con 2 lineas
		 */
		
		Flux<Linea> nflux = repo.findAll();
		
		StepVerifier.create(nflux)
			.assertNext(l -> {
				assertEquals("nombre_linea_1", l.getNombre());
			})
			.assertNext(l -> {
				assertEquals("nombre_linea_2", l.getNombre());
			})
			.expectComplete()
			.verify();
		
		Mono<Long> borradas = repo.deleteByIdReturningDeletedCount("id_linea_1");
		
		assertEquals(1L, borradas.block());
		
		StepVerifier.create(nflux)
			.assertNext(line -> {
				assertEquals("nombre_linea_2", line.getNombre());
			})
			.expectComplete()
			.verify();	
	}
	
	@Test
	void testRemoveVariosCampos() {
		
		repo.deleteAll().block();
		
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
		campo2.setAtributoId("atributo_id_2");
		campos.add(campo2);
		Campo<String> campo3 = new Campo<>();
		campo3.setId("campo_3_id");
		campo3.setDatos("datos_en_campo_3");
		campo3.setAtributoId("atributo_id_3");
		campos.add(campo3);
		
		Linea linea = new Linea();
		linea.setCampos(campos);
		linea.setId(id);
		linea.setNombre(name);
		
		repo.save(linea).block();
		
		Mono<Linea> mono = repo.findById(id);
		
		StepVerifier.create(mono)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(3, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
				assertEquals(campo2, lineo.getCampoByIndex(1));
				assertEquals(campo3, lineo.getCampoByIndex(2));
			})
			.expectComplete()
			.verify();
		
		repo.removeVariosCampos(id, new Campo<?>[] {campo1, campo2}).block();
		
		Mono<Linea> mono2 = repo.findById(id);
		
		StepVerifier.create(mono2)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(1, lineo.getCantidadCampos());
				assertEquals(campo3, lineo.getCampoByIndex(0));
			})
			.expectComplete()
			.verify();
		
	}
	
	@Test
	void testAddVariosCampos() {
		
		repo.deleteAll().block();
		
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
		campo2.setAtributoId("atributo_id_2");
		//campos.add(campo2);
		Campo<String> campo3 = new Campo<>();
		campo3.setId("campo_3_id");
		campo3.setDatos("datos_en_campo_3");
		campo3.setAtributoId("atributo_id_3");
		//campos.add(campo3);
		
		Linea linea = new Linea();
		linea.setCampos(campos);
		linea.setId(id);
		linea.setNombre(name);
		
		repo.save(linea).block();
		
		Mono<Linea> mono = repo.findById(id);
		
		StepVerifier.create(mono)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(1, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
				//assertEquals(campo2, line.getCampo(1));
				//assertEquals(campo3, line.getCampo(2));
			})
			.expectComplete()
			.verify();
		
		Long entradas = repo.addVariosCampos(id, Flux.just(campo2, campo3)).block();
		
		Mono<Linea> mono2 = repo.findById(id);
		
		StepVerifier.create(mono2)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(2, entradas);
				assertEquals(name, line.getNombre());
				assertEquals(3, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
				assertEquals(campo2, lineo.getCampoByIndex(1));
				assertEquals(campo3, lineo.getCampoByIndex(2));
			})
			.expectComplete()
			.verify();
		
	}
	
	@Test
	void testUpdateVariosCampos() {
		
		repo.deleteAll().block();
		
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
		campo2.setAtributoId("atributo_id_2");
		campos.add(campo2);
		Campo<String> campo3 = new Campo<>();
		campo3.setId("campo_3_id");
		campo3.setDatos("datos_en_campo_3");
		campo3.setAtributoId("atributo_id_3");
		campos.add(campo3);
		
		Linea linea = new Linea();
		linea.setCampos(campos);
		linea.setId(id);
		linea.setNombre(name);
		
		repo.save(linea).block();
		
		Mono<Linea> mono = repo.findById(id);
		
		StepVerifier.create(mono)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(name, line.getNombre());
				assertEquals(3, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
				assertEquals(campo2, lineo.getCampoByIndex(1));
				assertEquals(campo3, lineo.getCampoByIndex(2));
			})
			.expectComplete()
			.verify();
		
		/**
		 * Campos a modificar
		 */
		
		Campo<String> campo2re = new Campo<>();
		campo2re.setId("campo_2_id");
		campo2re.setDatos("datos_actualizados_en_campo_2");
		campo2re.setAtributoId("atributo_id_2");
		Campo<String> campo3re = new Campo<>();
		campo3re.setId("campo_3_id");
		campo3re.setDatos("datos_actualizados_en_campo_3");
		campo3re.setAtributoId("atributo_id_3");
		
		Long entradas = repo.updateVariosCampos(id, Flux.just(campo2re, campo3re)).block();
		
		Mono<Linea> mono2 = repo.findById(id);
		
		StepVerifier.create(mono2)
			.assertNext(line -> {
				LineaOperations lineo = new LineaOperations(line);
				assertEquals(2, entradas);
				assertEquals(name, line.getNombre());
				assertEquals(3, lineo.getCantidadCampos());
				assertEquals(campo1, lineo.getCampoByIndex(0));
				assertEquals(campo2re, lineo.getCampoByIndex(1));
				assertEquals(campo3re, lineo.getCampoByIndex(2));
				assertNotEquals(campo2, lineo.getCampoByIndex(1));
				assertNotEquals(campo3, lineo.getCampoByIndex(2));
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	void testUpdateNombre() {
		Linea resultado = repo.updateNombre(linea.getId(), "nombre actualizado").block();
		LineaOperations resultadoO = new LineaOperations(resultado);
		
		StepVerifier.create(mono)
		.assertNext(line -> {
			assertEquals(3, resultadoO.getCantidadCampos());
			assertEquals("nombre actualizado", resultado.getNombre());
			assertEquals("nombre actualizado", line.getNombre());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateOrder() {
		Linea resultado = repo.updateOrder(linea.getId(), 4).block();
		LineaOperations resultadoO = new LineaOperations(resultado);
		
		StepVerifier.create(mono)
		.assertNext(line -> {
			assertEquals(3, resultadoO.getCantidadCampos());
			assertEquals(4, line.getOrder());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateParentId() {
		Linea resultado = repo.updateParentId(linea.getId(), "new parent id").block();
		LineaOperations resultadoO = new LineaOperations(resultado);
		
		StepVerifier.create(mono)
		.assertNext(line -> {
			assertEquals(3, resultadoO.getCantidadCampos());
			assertEquals("new parent id", line.getParentId());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateCounterLineId() {
		List<String> list = new ArrayList<>();
		list.add("new counter id");
		Linea resultado = repo.updateCounterLineId(linea.getId(), list).block();
		LineaOperations resultadoO = new LineaOperations(resultado);
		
		StepVerifier.create(mono)
		.assertNext(line -> {
			assertEquals(3, line.operations().getCantidadCampos());
			assertEquals("new counter id", line.getCounterLineId().get(0));
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testDeleteSeveralLineasByPropuestaId() {
		repo.deleteSeveralLineasByPropuestaId(l1.getPropuestaId()).block();
		StepVerifier.create(flux)
		.assertNext(line -> assertEquals(linea.getPropuestaId(), line.getPropuestaId()))
		//.assertNext(l -> assertEquals(l1.getPropuestaId(), l.getPropuestaId()))
		//.assertNext(l -> assertEquals(l2.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l3.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l4.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l5.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l6.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l7.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l8.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l9.getPropuestaId(), l.getPropuestaId()))
		.expectComplete()
		.verify();
	}
	
	@Test
	void testDeleteSeveralLineasBySeveralPropuestaIds() {
		repo.deleteSeveralLineasBySeveralPropuestaIds(Arrays.asList(l1.getPropuestaId(), l3.getPropuestaId())).block();
		StepVerifier.create(flux)
		.assertNext(line -> assertEquals(linea.getPropuestaId(), line.getPropuestaId()))
		//.assertNext(l -> assertEquals(l1.getPropuestaId(), l.getPropuestaId()))
		//.assertNext(l -> assertEquals(l2.getPropuestaId(), l.getPropuestaId()))
		//.assertNext(l -> assertEquals(l3.getPropuestaId(), l.getPropuestaId()))
		//.assertNext(l -> assertEquals(l4.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l5.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l6.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l7.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l8.getPropuestaId(), l.getPropuestaId()))
		.assertNext(l -> assertEquals(l9.getPropuestaId(), l.getPropuestaId()))
		.expectComplete()
		.verify();
	}
	
	@Test
	void testAddCounterLineId() {
		Mono<Linea> mono = repo.addCounterLineId(linea.getId(), "counterLineId");
		
		StepVerifier.create(mono)
			.assertNext(line -> {
				assertEquals(2, line.getCounterLineId().size());
				assertEquals("counterLineId", line.getCounterLineId().get(1));
			})
			.expectComplete()
			.verify()
			;
		
		mono = repo.addCounterLineId(linea.getId(), "otra");
		
		StepVerifier.create(mono)
			.assertNext(line -> {
				assertEquals(3, line.getCounterLineId().size());
				assertEquals("otra", line.getCounterLineId().get(2));
			})
			.expectComplete()
			.verify()
			;
	}
	
	@Test
	void testRemoveCounterLineId() {
		Mono<Linea> mono = repo.findById(linea.getId());
		StepVerifier.create(mono)
		.assertNext(line -> {
			assertEquals(1, line.getCounterLineId().size());
		})
		.expectComplete()
		.verify()
		;
		
		mono = repo.removeCounterLineId(linea.getId(), linea.getCounterLineId().get(0));
		StepVerifier.create(mono)
		.assertNext(line -> {
			assertEquals(0, line.getCounterLineId().size());
		})
		.expectComplete()
		.verify()
		;
	}
	
	@Test
	void testUpdatePvps() {
		List<PvperLinea> pvps = new ArrayList<>();
		var pvp1 = new PvperLinea();
		var pvp2 = new PvperLinea();
		pvps.add(pvp1);
		pvps.add(pvp2);
		
		Linea resultado = repo.updatePvps(linea.getId(), pvps).block();
		StepVerifier.create(mono)
		.assertNext(l -> {
			assertEquals(2, l.getPvps().size());
			assertEquals(pvp1.getPvperId(), l.getPvps().get(0).getPvperId());
			assertEquals(pvp2.getPvperId(), l.getPvps().get(1).getPvperId());
		})
		.expectComplete()
		.verify()
		;
	}
	
	@Test
	void testUpdateCosts() {
		List<CosteLineaProveedor> costs = new ArrayList<>();
		var cost1 = new CosteLineaProveedor();
		var cost2 = new CosteLineaProveedor();
		costs.add(cost1);
		costs.add(cost2);
		
		Linea resultado = repo.updateCosts(linea.getId(), costs).block();
		
		StepVerifier.create(mono)
		.assertNext(l -> {
			assertEquals(2, l.getCostesProveedor().size());
			assertEquals(cost1.getCosteProveedorId(), l.getCostesProveedor().get(0).getCosteProveedorId());
			assertEquals(cost2.getCosteProveedorId(), l.getCostesProveedor().get(0).getCosteProveedorId());
		})
		;
	}

}
