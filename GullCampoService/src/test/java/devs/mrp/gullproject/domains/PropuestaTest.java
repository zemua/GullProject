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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.service.linea.LineaOperations;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@Slf4j
@Import({LineaFactory.class})
class PropuestaTest {
	
	@Autowired LineaFactory lineaFactory;
	
	List<String> lineas = new ArrayList<>();
	Linea l1;
	Linea l2;
	Linea l3;
	Linea l4;
	
	Propuesta propuesta;
	
	Campo<String> c1;
	Campo<Integer> c2;
	Campo<String> c3;
	Campo<Integer> c4;
	Campo<String> c5;
	Campo<Integer> c6;
	
	AtributoForCampo att1;
	AtributoForCampo att2;
	AtributoForCampo att3;
	
	@BeforeEach
	void inicializar() {
		c1 = new Campo<>();
		c1.setAtributoId("a1");
		c1.setDatos("datos1");
		c1.setId(new ObjectId().toString());
		c2 = new Campo<>();
		c2.setAtributoId("a2");
		c2.setDatos(123);
		c2.setId(new ObjectId().toString());
		List<Campo<?>> campos1 = new ArrayList<>();
		campos1.add(c1);
		campos1.add(c2);
		l1 = lineaFactory.create();
		l1.setCampos(campos1);
		l1.setId("l1id");
		l1.setNombre("nombre linea 1");
		
		c3 = new Campo<>();
		c3.setAtributoId("a1");
		c3.setDatos("datos1");
		c3.setId(new ObjectId().toString());
		c4 = new Campo<>();
		c4.setAtributoId("a2");
		c4.setDatos(123);
		c4.setId(new ObjectId().toString());
		List<Campo<?>> campos2 = new ArrayList<>();
		campos2.add(c3);
		campos2.add(c4);
		l2 = lineaFactory.create();
		l2.setCampos(campos2);
		l2.setId("l2id");
		l2.setNombre("nombre linea 2");
		
		lineas.clear();
		lineas.add(l1.getId());
		lineas.add(l2.getId());
		
		propuesta = new PropuestaCliente();
		propuesta.setLineaIds(lineas);
		
		c5 = new Campo<>();
		c5.setAtributoId("a1");
		c5.setDatos("datos2");
		c5.setId(new ObjectId().toString());
		c6 = new Campo<>();
		c6.setAtributoId("a2");
		c6.setDatos(321);
		c6.setId(new ObjectId().toString());
		List<Campo<?>> campos3 = new ArrayList<>();
		campos3.add(c5);
		campos3.add(c6);
		l3 = lineaFactory.create();
		l3.setCampos(campos3);
		l3.setId("l3id");
		l3.setNombre("nombre linea 1");
		
		List<Campo<?>> campos4 = new ArrayList<>();
		campos4.add(c3);
		campos4.add(c6);
		l4 = lineaFactory.create();
		l4.setCampos(campos4);
		l4.setId("l4id");
		l4.setNombre("nombre linea 4");
		
		att1 = new AtributoForCampo();
		att1.setId("a1");
		att1.setName("nameAtt1");
		att1.setTipo("DESCRIPCION");
		
		att2 = new AtributoForCampo();
		att2.setId("a2");
		att2.setName("nameAtt2");
		att2.setTipo("CANTIDAD");
		
		att3 = new AtributoForCampo();
		att3.setId("a3");
		att3.setName("nameAtt3");
		att3.setTipo("RANDOM");
		
		assertEquals(2, propuesta.operations().getCantidadLineaIds());
		assertEquals(0, propuesta.getAttributeColumns().size());
		propuesta.operations().addAttribute(att1);
		assertEquals(1, propuesta.getAttributeColumns().size());
		propuesta.operations().addAttribute(att2);
		assertEquals(2, propuesta.getAttributeColumns().size());
		assertEquals(att1, propuesta.getAttributeColumns().get(0));
		assertEquals(att2, propuesta.getAttributeColumns().get(1));
	}

	@Test
	void testObjectId() {
		Propuesta sc = new PropuestaCliente();
		log.debug(sc.getId());
		
		Pattern pattern = Pattern.compile("[[a-z]|[A-Z]|[0-9]]{24}");
	    Matcher matcher = pattern.matcher(sc.getId()); 
	    assertTrue(matcher.find());
	    assertEquals(24, sc.getId().length());
	}
	
	@Test
	void testUpdateLinea() {
		Linea l3 = lineaFactory.create();
		l3.setId("l3id");
		l3.setNombre("nombre linea 3");
		
		assertEquals("l1id", lineas.get(0));
		assertEquals("l2id", lineas.get(1));
		
		propuesta.operations().updateLineaId(l1.getId(), l3.getId());
		
		assertEquals("l3id", lineas.get(0));
		assertEquals("l2id", lineas.get(1));
		
		propuesta.operations().updateLineaId(l2.getId(), l3.getId());
		
		assertEquals("l3id", lineas.get(0));
		assertEquals("l3id", lineas.get(1));
	}
	
	@Test
	void testConfirmaIguales() {	
		assertTrue(LineaOperations.confirmaIguales(l1, l2));
		assertFalse(LineaOperations.confirmaIguales(l1, l3));
		assertFalse(LineaOperations.confirmaIguales(l1, l4));
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
		
		
		assertTrue(LineaOperations.confirmaIgualesSegunCampos(l1, l2, atributos));
		assertTrue(LineaOperations.confirmaIgualesSegunCampos(l1, l2, primerAtributo));
		assertTrue(LineaOperations.confirmaIgualesSegunCampos(l1, l2, segundoAtributo));
		assertFalse(LineaOperations.confirmaIgualesSegunCampos(l1, l3, atributos));
		assertFalse(LineaOperations.confirmaIgualesSegunCampos(l1, l3, primerAtributo));
		assertFalse(LineaOperations.confirmaIgualesSegunCampos(l1, l3, segundoAtributo));
		assertFalse(LineaOperations.confirmaIgualesSegunCampos(l1, l4, atributos));
		assertTrue(LineaOperations.confirmaIgualesSegunCampos(l1, l4, primerAtributo));
		assertFalse(LineaOperations.confirmaIgualesSegunCampos(l1, l4, segundoAtributo));
		
	}
	
	@Test
	void testAssertMatchesCampos() {
		Map<String, Object> map = new HashMap<>();
		map.put("a1", "datos1");
		assertTrue(LineaOperations.assertMatchesCampos(l4, map));
		map.put("a2", Integer.valueOf(123));
		map.put("a3", "");
		map.put("a4", Integer.valueOf(0));
		map.put("a5", Long.valueOf(0));
		map.put("a6", Double.valueOf(0));
		map.put("a7", Float.valueOf(0));
		map.put("a8", Boolean.valueOf(false));
		
		assertTrue(LineaOperations.assertMatchesCampos(l1, map));
		assertTrue(LineaOperations.assertMatchesCampos(l2, map));
		assertFalse(LineaOperations.assertMatchesCampos(l3, map));
		assertFalse(LineaOperations.assertMatchesCampos(l4, map));
		
		map.put("a9", Boolean.valueOf(true));
		
		assertFalse(LineaOperations.assertMatchesCampos(l1, map));
		assertFalse(LineaOperations.assertMatchesCampos(l2, map));
	}
	
	@Test
	void testAssertMatchesCamposEstricto() {
		Map<String, Object> map = new HashMap<>();
		map.put("a1", "datos1");
		assertTrue(LineaOperations.assertMatchesCamposEstricto(l4, map));
		map.put("a2", Integer.valueOf(123));
		
		assertTrue(LineaOperations.assertMatchesCamposEstricto(l1, map));
		assertTrue(LineaOperations.assertMatchesCamposEstricto(l2, map));
		assertFalse(LineaOperations.assertMatchesCamposEstricto(l3, map));
		assertFalse(LineaOperations.assertMatchesCamposEstricto(l4, map));
		
		map.put("a3", "");
		
		assertFalse(LineaOperations.assertMatchesCamposEstricto(l1, map));
		assertFalse(LineaOperations.assertMatchesCamposEstricto(l2, map));
	}
	
	@Test
	void testAddAttribute() {
		propuesta.operations().addAttribute(att3);
		assertEquals(3, propuesta.getAttributeColumns().size());
		assertEquals(att3, propuesta.getAttributeColumns().get(2));
	}
	
	@Test
	void testRemoveAttribute() {
		propuesta.operations().removeAttribute(att2);
		assertEquals(1, propuesta.getAttributeColumns().size());
		assertEquals(att1, propuesta.getAttributeColumns().get(0));
	}
	
	@Test
	void testRemoveAttributeById() {
		propuesta.operations().removeAttributeById(att1.getId());
		assertEquals(1, propuesta.getAttributeColumns().size());
		assertEquals(att2, propuesta.getAttributeColumns().get(0));
	}

}
