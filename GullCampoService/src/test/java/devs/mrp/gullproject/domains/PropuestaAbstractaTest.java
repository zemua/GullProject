package devs.mrp.gullproject.domains;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@Slf4j
class PropuestaAbstractaTest {
	
	List<String> lineas = new ArrayList<>();
	Linea l1;
	Linea l2;
	Linea l3;
	Linea l4;
	
	PropuestaAbstracta propuesta;
	
	@BeforeEach
	void inicializar() {
		Campo<String> c1 = new Campo<>();
		c1.setAtributoId("a1");
		c1.setDatos("datos1");
		c1.setId(new ObjectId().toString());
		Campo<Integer> c2 = new Campo<>();
		c2.setAtributoId("a2");
		c2.setDatos(123);
		c2.setId(new ObjectId().toString());
		List<Campo<?>> campos1 = new ArrayList<>();
		campos1.add(c1);
		campos1.add(c2);
		l1 = new Linea();
		l1.setCampos(campos1);
		l1.setId("l1id");
		l1.setNombre("nombre linea 1");
		
		Campo<String> c3 = new Campo<>();
		c3.setAtributoId("a1");
		c3.setDatos("datos1");
		c3.setId(new ObjectId().toString());
		Campo<Integer> c4 = new Campo<>();
		c4.setAtributoId("a2");
		c4.setDatos(123);
		c4.setId(new ObjectId().toString());
		List<Campo<?>> campos2 = new ArrayList<>();
		campos2.add(c3);
		campos2.add(c4);
		l2 = new Linea();
		l2.setCampos(campos2);
		l2.setId("l2id");
		l2.setNombre("nombre linea 2");
		
		lineas.clear();
		lineas.add(l1.getId());
		lineas.add(l2.getId());
		
		propuesta = new PropuestaCliente();
		propuesta.setLineaIds(lineas);
		
		Campo<String> c5 = new Campo<>();
		c5.setAtributoId("a1");
		c5.setDatos("datos2");
		c5.setId(new ObjectId().toString());
		Campo<Integer> c6 = new Campo<>();
		c6.setAtributoId("a2");
		c6.setDatos(321);
		c6.setId(new ObjectId().toString());
		List<Campo<?>> campos3 = new ArrayList<>();
		campos3.add(c5);
		campos3.add(c6);
		l3 = new Linea();
		l3.setCampos(campos3);
		l3.setId("l3id");
		l3.setNombre("nombre linea 1");
		
		List<Campo<?>> campos4 = new ArrayList<>();
		campos4.add(c3);
		campos4.add(c6);
		l4 = new Linea();
		l4.setCampos(campos4);
		l4.setId("l4id");
		l4.setNombre("nombre linea 4");
	}

	@Test
	void testObjectId() {
		PropuestaAbstracta sc = new PropuestaCliente();
		log.debug(sc.getId());
		
		Pattern pattern = Pattern.compile("[[a-z]|[A-Z]|[0-9]]{24}");
	    Matcher matcher = pattern.matcher(sc.getId()); 
	    assertTrue(matcher.find());
	    assertEquals(24, sc.getId().length());
	}
	
	@Test
	void testUpdateLinea() {
		Linea l3 = new Linea();
		l3.setId("l3id");
		l3.setNombre("nombre linea 3");
		
		assertEquals("l1id", lineas.get(0));
		assertEquals("l2id", lineas.get(1));
		
		propuesta.updateLineaId(l1.getId(), l3.getId());
		
		assertEquals("l3id", lineas.get(0));
		assertEquals("l2id", lineas.get(1));
		
		propuesta.updateLineaId(l2.getId(), l3.getId());
		
		assertEquals("l3id", lineas.get(0));
		assertEquals("l3id", lineas.get(1));
	}
	
	@Test
	void testConfirmaIguales() {	
		assertTrue(Propuesta.confirmaIguales(l1, l2));
		assertFalse(Propuesta.confirmaIguales(l1, l3));
		assertFalse(Propuesta.confirmaIguales(l1, l4));
	}
	
	@Test
	void testConfirmaIgualesSegunCampos() {
		
		AtributoForCampo a1 = new AtributoForCampo();
		a1.setId("a1");
		AtributoForCampo a2 = new AtributoForCampo();
		a2.setId("a2");
		
		ArrayList<AtributoForCampo> atributos = new ArrayList<>();
		atributos.add(a1);
		atributos.add(a2);
		
		ArrayList<AtributoForCampo> primerAtributo = new ArrayList<>();
		primerAtributo.add(a1);
		
		ArrayList<AtributoForCampo> segundoAtributo = new ArrayList<>();
		segundoAtributo.add(a2);
		
		
		assertTrue(Propuesta.confirmaIgualesSegunCampos(l1, l2, atributos));
		assertTrue(Propuesta.confirmaIgualesSegunCampos(l1, l2, primerAtributo));
		assertTrue(Propuesta.confirmaIgualesSegunCampos(l1, l2, segundoAtributo));
		assertFalse(Propuesta.confirmaIgualesSegunCampos(l1, l3, atributos));
		assertFalse(Propuesta.confirmaIgualesSegunCampos(l1, l3, primerAtributo));
		assertFalse(Propuesta.confirmaIgualesSegunCampos(l1, l3, segundoAtributo));
		assertFalse(Propuesta.confirmaIgualesSegunCampos(l1, l4, atributos));
		assertTrue(Propuesta.confirmaIgualesSegunCampos(l1, l4, primerAtributo));
		assertFalse(Propuesta.confirmaIgualesSegunCampos(l1, l4, segundoAtributo));
		
	}
	
	@Test
	void testAssertMatchesCampos() {
		Map<String, Object> map = new HashMap<>();
		map.put("a1", "datos1");
		assertTrue(Propuesta.assertMatchesCampos(l4, map));
		map.put("a2", Integer.valueOf(123));
		map.put("a3", "");
		map.put("a4", Integer.valueOf(0));
		map.put("a5", Long.valueOf(0));
		map.put("a6", Double.valueOf(0));
		map.put("a7", Float.valueOf(0));
		map.put("a8", Boolean.valueOf(false));
		
		assertTrue(Propuesta.assertMatchesCampos(l1, map));
		assertTrue(Propuesta.assertMatchesCampos(l2, map));
		assertFalse(Propuesta.assertMatchesCampos(l3, map));
		assertFalse(Propuesta.assertMatchesCampos(l4, map));
		
		map.put("a9", Boolean.valueOf(true));
		
		assertFalse(Propuesta.assertMatchesCampos(l1, map));
		assertFalse(Propuesta.assertMatchesCampos(l2, map));
	}
	
	@Test
	void testAssertMatchesCamposEstricto() {
		Map<String, Object> map = new HashMap<>();
		map.put("a1", "datos1");
		assertTrue(Propuesta.assertMatchesCamposEstricto(l4, map));
		map.put("a2", Integer.valueOf(123));
		
		assertTrue(Propuesta.assertMatchesCamposEstricto(l1, map));
		assertTrue(Propuesta.assertMatchesCamposEstricto(l2, map));
		assertFalse(Propuesta.assertMatchesCamposEstricto(l3, map));
		assertFalse(Propuesta.assertMatchesCamposEstricto(l4, map));
		
		map.put("a3", "");
		
		assertFalse(Propuesta.assertMatchesCamposEstricto(l1, map));
		assertFalse(Propuesta.assertMatchesCamposEstricto(l2, map));
	}

}
