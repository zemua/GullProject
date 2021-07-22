package devs.mrp.gullproject.service.propuesta.oferta;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.Pvper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class IncludeCotizacionCheckerTest {

	@Autowired IncludeCotizacionCheckerFactory checkerFactory;
	
	@Test
	void test() {
		CosteProveedor cost1 = new CosteProveedor();
		PropuestaProveedor prop1 = new PropuestaProveedor();
		prop1.setCostes(new ArrayList<>() {{add(cost1);}});
		
		CosteProveedor cost2 = new CosteProveedor();
		PropuestaProveedor prop2 = new PropuestaProveedor();
		prop2.setCostes(new ArrayList<>() {{add(cost2);}});
		
		Consulta consulta = new Consulta();
		consulta.setPropuestas(new ArrayList<>() {{add(prop1);add(prop2);}});
		
		Pvper pvp = new Pvper();
		pvp.setIdCostes(new ArrayList<>() {{add(cost1.getId());}});
		
		var checker = checkerFactory.from(pvp, consulta);
		
		assertTrue(checker.ifIncludes(prop1.getId()));
		assertFalse(checker.ifIncludes(prop2.getId()));
	}

}
