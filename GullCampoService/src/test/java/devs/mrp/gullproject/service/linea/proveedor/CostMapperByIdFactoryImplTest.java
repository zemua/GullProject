package devs.mrp.gullproject.service.linea.proveedor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CostMapperByIdFactoryImplTest {

	@Autowired CostMapperByIdFactory factory;
	
	@Test
	void test() {
		List<Linea> lineas = new ArrayList<>();
		
		Linea l1 = new Linea();
		l1.setCounterLineId(new ArrayList<>() {{add("counter1a");add("counter1b");}});
		CosteLineaProveedor cost1a = new CosteLineaProveedor();
		cost1a.setCosteProveedorId("cost1aid");
		cost1a.setValue(789);
		CosteLineaProveedor cost1b = new CosteLineaProveedor();
		cost1b.setCosteProveedorId("cost1bid");
		cost1b.setValue(456);
		l1.setCostesProveedor(new ArrayList<>() {{add(cost1a);add(cost1b);}});
		
		Linea l2 = new Linea();
		l2.setCounterLineId(new ArrayList<>() {{add("counter2");}});
		CosteLineaProveedor cost2 = new CosteLineaProveedor();
		cost2.setCosteProveedorId("cost2id");
		cost2.setValue(123);
		l2.setCostesProveedor(new ArrayList<>() {{add(cost2);}});
		
		Linea l3 = new Linea();
		l3.setCounterLineId(new ArrayList<>() {{add("counter3");}});
		CosteLineaProveedor cost3 = new CosteLineaProveedor();
		cost3.setCosteProveedorId("cost3id");
		cost3.setValue(963);
		l3.setCostesProveedor(new ArrayList<>() {{add(cost3);}});
		
		lineas.add(l1);
		lineas.add(l2);
		lineas.add(l3);
		
		var mapper = factory.from(lineas);
		
		assertEquals(789, mapper.getByDupla("counter1a", "cost1aid"));
		assertEquals(789, mapper.getByDupla("counter1b", "cost1aid"));
		assertEquals(456, mapper.getByDupla("counter1a", "cost1bid"));
		assertEquals(456, mapper.getByDupla("counter1b", "cost1bid"));
		assertEquals(123, mapper.getByDupla("counter2", "cost2id"));
		assertEquals(963, mapper.getByDupla("counter3", "cost3id"));
		assertEquals(0, mapper.getByDupla("counter1a", "cost2id"));
	}

}
