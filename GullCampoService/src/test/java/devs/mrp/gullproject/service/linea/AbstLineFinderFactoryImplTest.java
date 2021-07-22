package devs.mrp.gullproject.service.linea;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstractaFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AbstLineFinderFactoryImplTest {

	@Autowired AbstLineFinderFactory finderFactory;
	@Autowired LineaAbstractaFactory lineFactory;
	
	@Test
	void test() {
		List<LineaAbstracta> lineas = new ArrayList<>();
		var l1 = lineFactory.create();
		var l2 = lineFactory.create();
		var l3 = lineFactory.create();
		l1.setCounterLineId("counter1");
		l2.setCounterLineId("counter2");
		l3.setCounterLineId("counter2");
		lineas.add(l1);
		lineas.add(l2);
		lineas.add(l3);
		
		var finder = finderFactory.from(lineas);
		
		var ls1 = finder.findBy("counter1");
		assertEquals(1, ls1.size());
		assertEquals(l1, ls1.get(0));
		
		var ls2 = finder.findBy("counter2");
		assertEquals(2, ls2.size());
		assertEquals(l2, ls2.get(0));
		assertEquals(l3, ls2.get(1));
	}

}
