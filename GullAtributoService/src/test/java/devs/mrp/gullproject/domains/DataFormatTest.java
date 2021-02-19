package devs.mrp.gullproject.domains;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DataFormatTest {

	@Test
	void testGetTipo() {
		
		DataFormat df = DataFormat.CANTIDAD;
		assertEquals("Integer", df.getTipo());
		
		df = DataFormat.COSTE;
		assertEquals("Double", df.getTipo());
		
		df = DataFormat.DESCRIPCION;
		assertEquals("String", df.getTipo());
		
		df = DataFormat.MARGEN;
		assertEquals("Float", df.getTipo());
		
		df = DataFormat.PLAZO;
		assertEquals("Long", df.getTipo());
		
		df = DataFormat.PVP;
		assertEquals("Double", df.getTipo());
		
	}
	
	@Test
	void testCheckIfValidValue() {
		
		DataFormat df = DataFormat.valueOf("CANTIDAD");
		assertTrue(df.checkIfValidValue("12345"));
		assertFalse(df.checkIfValidValue("invalido"));
		assertFalse(df.checkIfValidValue("123,45"));
		assertFalse(df.checkIfValidValue("123.45"));
		assertFalse(df.checkIfValidValue("123b"));
		
		df = DataFormat.COSTE;
		assertTrue(df.checkIfValidValue("123.45"));
		assertFalse(df.checkIfValidValue("123,45"));
		assertFalse(df.checkIfValidValue("invalido"));
		assertTrue(df.checkIfValidValue("12345"));
		
		df = DataFormat.DESCRIPCION;
		assertTrue(df.checkIfValidValue("probando"));
		assertTrue(df.checkIfValidValue("12345"));
		assertTrue(df.checkIfValidValue("123.45"));
		
		df = DataFormat.MARGEN;
		assertTrue(df.checkIfValidValue("123.45"));
		assertFalse(df.checkIfValidValue("123,45"));
		assertFalse(df.checkIfValidValue("invalido"));
		assertTrue(df.checkIfValidValue("12345"));
		assertFalse(df.checkIfValidValue("a1234.56"));
		
		df = DataFormat.PLAZO;
		assertTrue(df.checkIfValidValue("1234567890"));
		assertFalse(df.checkIfValidValue("123.45"));
		assertFalse(df.checkIfValidValue("123,45"));
		assertFalse(df.checkIfValidValue("invalido"));
		assertFalse(df.checkIfValidValue("123d"));
		
		df = DataFormat.PVP;
		assertTrue(df.checkIfValidValue("123.45"));
		assertFalse(df.checkIfValidValue("asdf"));
		assertTrue(df.checkIfValidValue("123"));
		assertFalse(df.checkIfValidValue("123b"));
		assertFalse(df.checkIfValidValue("123,45"));
		
	}

}
