package devs.mrp.gullproject.service.propuesta.proveedor;

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

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CotizacionOfCostMapperTest {

	@Autowired CotizacionOfCostMapperFactory mapperFactory;
	
	@Test
	void test() {
		CosteProveedor cost1a = new CosteProveedor();
		cost1a.setName("cost1a");
		CosteProveedor cost1b = new CosteProveedor();
		cost1b.setName("cost1b");
		PropuestaProveedor prop1 = new PropuestaProveedor();
		prop1.setCostes(new ArrayList<>() {{add(cost1a);add(cost1b);}});
		
		CosteProveedor cost2 = new CosteProveedor();
		cost2.setName("");
		PropuestaProveedor prop2 = new PropuestaProveedor();
		prop2.setCostes(new ArrayList<>() {{add(cost2);}});
		
		Consulta consulta = new Consulta();
		consulta.setPropuestas(new ArrayList<>() {{add(prop1);add(prop2);}});
		
		var mapper = mapperFactory.from(consulta);
		
		assertEquals(prop1.getId(), mapper.of(cost1a.getId()));
		assertEquals(prop1.getId(), mapper.of(cost1b.getId()));
		assertEquals(prop2.getId(), mapper.of(cost2.getId()));
	}

}
