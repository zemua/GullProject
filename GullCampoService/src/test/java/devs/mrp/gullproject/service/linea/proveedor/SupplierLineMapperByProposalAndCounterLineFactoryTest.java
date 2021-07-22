package devs.mrp.gullproject.service.linea.proveedor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.Linea;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SupplierLineMapperByProposalAndCounterLineFactoryTest {

	@Autowired SupplierLineMapperByProposalAndCounterLineFactory factory;
	
	@Test
	void test() {
		List<Linea> lineas = new ArrayList<>();
		Linea l1 = new Linea();
		l1.setPropuestaId("propid1");
		l1.setCounterLineId(new ArrayList<>() {{add("counter1a");add("counter1b");}});
		Linea l2 = new Linea();
		l2.setPropuestaId("propid1");
		l2.setCounterLineId(new ArrayList<>() {{add("counter2");}});
		Linea l3 = new Linea();
		l3.setPropuestaId("propid2");
		l3.setCounterLineId(new ArrayList<>() {{add("counter2");}});
		
		lineas.add(l1);
		lineas.add(l2);
		lineas.add(l3);
		
		var mapper = factory.from(lineas);
		
		assertEquals(l1, mapper.getByDupla("propid1", "counter1a"));
		assertEquals(l1, mapper.getByDupla("propid1", "counter1b"));
		assertEquals(l2, mapper.getByDupla("propid1", "counter2"));
		assertEquals(l3, mapper.getByDupla("propid2", "counter2"));
		assertEquals(null, mapper.getByDupla("propid2", "counter1a"));
	}

}
