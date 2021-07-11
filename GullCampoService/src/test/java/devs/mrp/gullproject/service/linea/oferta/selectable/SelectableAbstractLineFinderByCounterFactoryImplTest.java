package devs.mrp.gullproject.service.linea.oferta.selectable;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstractaFactory;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLineFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SelectableAbstractLineFinderByCounterFactoryImplTest {

	@Autowired SelectableLineFactory selectableFactory;
	@Autowired SelectableAbstractLineFinderByCounterFactory finderFactory;
	
	@Test
	void test() {
		List<SelectableAbstractLine> lineas = new ArrayList<>();
		var l1 = selectableFactory.create();
		l1.setCounterLineId("counter1");
		var l2 = selectableFactory.create();
		l2.setCounterLineId("counter2");
		var l3 = selectableFactory.create();
		l3.setCounterLineId("counter2");
		lineas.add(l1);
		lineas.add(l2);
		lineas.add(l3);
		
		var finder = finderFactory.from(lineas);
		
		assertEquals(1, finder.find("counter1").size());
		assertEquals(l1, finder.find("counter1").get(0));
		assertEquals(2, finder.find("counter2").size());
		assertEquals(l2, finder.find("counter2").get(0));
		assertEquals(l3, finder.find("counter2").get(1));
	}

}
