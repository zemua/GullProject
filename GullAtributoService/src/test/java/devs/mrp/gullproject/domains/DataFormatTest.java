package devs.mrp.gullproject.domains;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DataFormatTest {

	@Test
	void testGetTipo() {
		
		DataFormat df = DataFormat.NUMERO;
		assertEquals("Integer", df.getTipo());
		
		df = DataFormat.DECIMAL;
		assertEquals("Double", df.getTipo());
		
		df = DataFormat.DESCRIPCION;
		assertEquals("String", df.getTipo());
		
	}
	
	@Test
	void testCheckIfValidValue() {
		
		DataFormat df = DataFormat.valueOf("NUMERO");
		assertTrue(df.checkIfValidValue("12345"));
		assertFalse(df.checkIfValidValue("invalido"));
		assertFalse(df.checkIfValidValue("123,45"));
		assertFalse(df.checkIfValidValue("123.45"));
		assertFalse(df.checkIfValidValue("123b"));
		
		df = DataFormat.DECIMAL;
		assertTrue(df.checkIfValidValue("123.45"));
		assertFalse(df.checkIfValidValue("123,45"));
		assertFalse(df.checkIfValidValue("invalido"));
		assertTrue(df.checkIfValidValue("12345"));
		
		df = DataFormat.DESCRIPCION;
		assertTrue(df.checkIfValidValue("probando"));
		assertTrue(df.checkIfValidValue("12345"));
		assertTrue(df.checkIfValidValue("123.45"));
		
	}

}
