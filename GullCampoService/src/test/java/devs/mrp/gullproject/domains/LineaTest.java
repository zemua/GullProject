package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.service.linea.LineaOperations;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@Slf4j
public class LineaTest {

	Linea linea;
	LineaOperations lineaOperations;
	Campo<String> campo1;
	Campo<Integer> campo2;
	Campo<Double> campo3;
	Campo<String> campo4;
	Campo<Integer> campo5;
	
	@BeforeEach
	void init() {
		campo1 = new Campo<>();
		campo1.setAtributoId("att1");
		campo1.setDatos("mis datos");
		campo1.setId("idcampo1");
		campo2 = new Campo<>();
		campo2.setAtributoId("att2");
		campo2.setDatos(12345);
		campo2.setId("idcampo2");
		campo3 = new Campo<>();
		campo3.setAtributoId("att3");
		campo3.setDatos(34.56);
		campo3.setId("idcampo3");
		
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(campo1);
		campos.add(campo2);
		campos.add(campo3);
		
		campo4 = new Campo<>();
		campo4.setAtributoId("att4");
		campo4.setDatos("datos campo 4");
		campo4.setId("idcampo4");
		campo5 = new Campo<>();
		campo5.setAtributoId("att5");
		campo5.setDatos(987);
		campo5.setId("idcampo5");
		
		linea = new Linea();
		linea.setCampos(campos);
		linea.setId("idlinea");
		linea.setNombre("nombre linea");
		linea.setPropuestaId("propuestaid");
		lineaOperations = new LineaOperations(linea);
	}
	
	@Test
	void testResetCampos() {
		List<Campo<Object>> campos = new ArrayList<>();
		Campo<Object> campo6 = new Campo<>();
		campo6.setId("id6");
		Campo<Object> campo7 = new Campo<>();
		campo7.setId("id7");
		campos.add(campo6);
		campos.add(campo7);
		lineaOperations.resetCampos(campos);
		assertEquals(2, lineaOperations.getCantidadCampos());
		assertEquals(campo6.getId(), lineaOperations.getCampoByIndex(0).getId());
		assertEquals(campo7.getId(), lineaOperations.getCampoByIndex(1).getId());
	}
	
	@Test
	void testGetMapOfCamposByAtributoId() {
		Map<String, Campo<?>> map = lineaOperations.getMapOfCamposByAtributoId();
		assertEquals(campo1.getId(), map.get(campo1.getAtributoId()).getId());
		assertEquals(campo2.getId(), map.get(campo2.getAtributoId()).getId());
		assertEquals(campo3.getId(), map.get(campo3.getAtributoId()).getId());
	}
	
	@Test
	void testGetCampoByAttId() {
		assertEquals(campo1, lineaOperations.getCampoByAttId(campo1.getAtributoId()));
		assertEquals(campo2, lineaOperations.getCampoByAttId(campo2.getAtributoId()));
		assertEquals(campo3, lineaOperations.getCampoByAttId(campo3.getAtributoId()));
		assertEquals("", lineaOperations.getCampoByAttId("atributo que no existe").getDatosText());
	}
	
	@Test
	void testGetValueByAttId() {
		assertEquals(campo1.getDatosText(), lineaOperations.getValueByAttId(campo1.getAtributoId()));
		assertEquals(campo2.getDatosText(), lineaOperations.getValueByAttId(campo2.getAtributoId()));
		assertEquals(campo3.getDatosText(), lineaOperations.getValueByAttId(campo3.getAtributoId()));
		assertEquals("", lineaOperations.getValueByAttId("atributo id que no existe"));
	}
	
	@Test
	void testReplaceCampo() {
		lineaOperations.replaceCampo(campo1.getAtributoId(), campo4);
		assertEquals(campo2, lineaOperations.getCampoByIndex(0));
		assertEquals(campo3, lineaOperations.getCampoByIndex(1));
		assertEquals(campo4, lineaOperations.getCampoByIndex(2));
		lineaOperations.replaceCampo(campo3);
		assertEquals(campo2, lineaOperations.getCampoByIndex(0));
		assertEquals(campo4, lineaOperations.getCampoByIndex(1));
		assertEquals(campo3, lineaOperations.getCampoByIndex(2));
	}
	
	@Test
	void testReplaceOrElseAddCampo() {
		lineaOperations.replaceOrElseAddCampo(campo1.getAtributoId(), campo4);
		assertEquals(3, lineaOperations.getCantidadCampos());
		assertEquals(campo2, lineaOperations.getCampoByIndex(0));
		assertEquals(campo3, lineaOperations.getCampoByIndex(1));
		assertEquals(campo4, lineaOperations.getCampoByIndex(2));
		
		lineaOperations.replaceOrElseAddCampo(campo5.getAtributoId(), campo5);
		assertEquals(4, lineaOperations.getCantidadCampos());
		assertEquals(campo2, lineaOperations.getCampoByIndex(0));
		assertEquals(campo3, lineaOperations.getCampoByIndex(1));
		assertEquals(campo4, lineaOperations.getCampoByIndex(2));
		assertEquals(campo5, lineaOperations.getCampoByIndex(3));
	}
	
	@Test
	void testReplaceOrElseAddCampos() {
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(campo4);
		campos.add(campo5);
		campos.add(campo1);
		lineaOperations.replaceOrElseAddCampos(campos);
		assertEquals(5, lineaOperations.getCantidadCampos());
		assertEquals(campo2, lineaOperations.getCampoByIndex(0));
		assertEquals(campo3, lineaOperations.getCampoByIndex(1));
		assertEquals(campo4, lineaOperations.getCampoByIndex(2));
		assertEquals(campo5, lineaOperations.getCampoByIndex(3));
		assertEquals(campo1, lineaOperations.getCampoByIndex(4));
	}
	
	@Test
	void testReplaceOrAddCamposObj() {
		List<Campo<Object>> campos = new ArrayList<>();
		Campo<Object> campo6 = new Campo<>();
		campo6.setId("id6");
		campo6.setAtributoId("att6");
		Campo<Object> campo7 = new Campo<>();
		campo7.setId("id7");
		campo7.setAtributoId(campo2.getAtributoId());
		campos.add(campo6);
		campos.add(campo7);
		
		lineaOperations.replaceOrAddCamposObj(campos);
		assertEquals(4, lineaOperations.getCantidadCampos());
		assertEquals(campo1, lineaOperations.getCampoByIndex(0));
		assertEquals(campo3, lineaOperations.getCampoByIndex(1));
		assertEquals(campo6, lineaOperations.getCampoByIndex(2));
		assertEquals(campo7, lineaOperations.getCampoByIndex(3));
	}
	
	@Test
	void testAddCampo() {
		lineaOperations.addCampo(campo4);
		assertEquals(4, lineaOperations.getCantidadCampos());
		assertEquals(campo1, lineaOperations.getCampoByIndex(0));
		assertEquals(campo2, lineaOperations.getCampoByIndex(1));
		assertEquals(campo3, lineaOperations.getCampoByIndex(2));
		assertEquals(campo4, lineaOperations.getCampoByIndex(3));
		lineaOperations.addCampo(campo1);
		assertEquals(5, lineaOperations.getCantidadCampos());
		assertEquals(campo1, lineaOperations.getCampoByIndex(0));
		assertEquals(campo2, lineaOperations.getCampoByIndex(1));
		assertEquals(campo3, lineaOperations.getCampoByIndex(2));
		assertEquals(campo4, lineaOperations.getCampoByIndex(3));
		assertEquals(campo1, lineaOperations.getCampoByIndex(4));
	}
	
	@Test
	void testRemoveCampoByAttId() {
		lineaOperations.removeCampoByAttId(campo2.getAtributoId());
		assertEquals(2, lineaOperations.getCantidadCampos());
		assertEquals(campo1, lineaOperations.getCampoByIndex(0));
		assertEquals(campo3, lineaOperations.getCampoByIndex(1));
	}
	
	@Test
	void testEquals() {
		Linea linea2 = new Linea();
		LineaOperations linea2operations = new LineaOperations(linea2);
		linea2.setNombre(linea.getNombre());
		linea2.setPropuestaId(linea.getPropuestaId());
		linea2operations.addCampo(campo1);
		linea2operations.addCampo(campo2);
		linea2operations.addCampo(campo3);
		assertTrue(lineaOperations.equals(linea2));
	}
	
}
