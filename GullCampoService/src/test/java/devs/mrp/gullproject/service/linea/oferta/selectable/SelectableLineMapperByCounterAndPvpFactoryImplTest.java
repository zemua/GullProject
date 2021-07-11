package devs.mrp.gullproject.service.linea.oferta.selectable;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLineFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SelectableLineMapperByCounterAndPvpFactoryImplTest {

	@Autowired SelectableLineFactory selectableFactory;
	@Autowired SelectableLineMapperByCounterAndPvpFactory mapperFactory;
	
	@Test
	void test() {
		List<SelectableAbstractLine> lineas = new ArrayList<>();
		
		var l1 = selectableFactory.create();
		l1.setCounterLineId("counter1");
		PvperLinea pvp1 = new PvperLinea();
		pvp1.setMargen(25);
		pvp1.setPvp(26);
		pvp1.setPvperId("pvpid1");
		l1.setPvp(pvp1);
		
		var l2 = selectableFactory.create();
		l2.setCounterLineId("counter2");
		PvperLinea pvp2 = new PvperLinea();
		pvp2.setMargen(15);
		pvp2.setPvp(16);
		pvp2.setPvperId("pvpid1");
		l2.setPvp(pvp2);
		
		var l3 = selectableFactory.create();
		l3.setCounterLineId("counter2");
		PvperLinea pvp3 = new PvperLinea();
		pvp3.setMargen(5);
		pvp3.setPvp(6);
		pvp3.setPvperId("pvpid2");
		l3.setPvp(pvp3);
		
		lineas.add(l1);
		lineas.add(l2);
		lineas.add(l3);
		
		var mapper = mapperFactory.from(lineas);
		
		assertEquals(l1, mapper.findBy("counter1", "pvpid1"));
		assertEquals(l2, mapper.findBy("counter2", "pvpid1"));
		assertEquals(l3, mapper.findBy("counter2", "pvpid2"));
	}

}
