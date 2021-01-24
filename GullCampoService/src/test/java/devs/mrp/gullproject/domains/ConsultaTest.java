package devs.mrp.gullproject.domains;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsultaTest {

	Consulta consulta;
	Propuesta propuesta1;
	Propuesta propuesta2;
	
	@BeforeEach
	void init() {
		propuesta1 = new PropuestaCliente();
		propuesta2 = new PropuestaCliente();
		
		consulta = new Consulta();
		consulta.addPropuesta(propuesta1);
		consulta.addPropuesta(propuesta2);
	}
	
	@Test
	void testGetPropuestaIndexByPropuestaId() {
		assertTrue(consulta.getPropuestaIndexByPropuestaId(propuesta1.getId()) == 0);
		assertTrue(consulta.getPropuestaIndexByPropuestaId(propuesta2.getId()) == 1);
	}
	
	@Test
	void testGetPropuestaById() {
		assertTrue(consulta.getPropuestaById(propuesta1.getId()).equals(propuesta1));
		assertTrue(consulta.getPropuestaById(propuesta2.getId()).equals(propuesta2));
	}

}
