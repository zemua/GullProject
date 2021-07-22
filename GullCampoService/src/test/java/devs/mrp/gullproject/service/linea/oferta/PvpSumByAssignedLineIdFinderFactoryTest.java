package devs.mrp.gullproject.service.linea.oferta;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PvperSum;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PvpSumByAssignedLineIdFinderFactoryTest {

	@Autowired PvpSumByAssignedLineIdFinderFactory factory;
	
	@Test
	void test() {
		PropuestaNuestra propuesta = new PropuestaNuestra();
		PvperSum sum1 = new PvperSum();
		sum1.setName("sum1name");
		sum1.setPvperIds(new ArrayList<>() {{add("pvpid1a");add("pvpid1b");}});
		PvperSum sum2 = new PvperSum();
		sum2.setName("sum2name");
		sum2.setPvperIds(new ArrayList<>() {{add("pvpid2");}});
		propuesta.setSums(new ArrayList<>() {{add(sum1);add(sum2);}});
		
		List<Linea> lineas = new ArrayList<>();
		Linea l1 = new Linea();
		l1.setCounterLineId(new ArrayList<>() {{add("counter1a");add("counter1b");}});
		PvperLinea pvp = new PvperLinea();
		pvp.setMargen(25.6);
		pvp.setPvp(85.4);
		pvp.setPvperId("pvpid1a");
		PvperLinea pvp1 = new PvperLinea();
		pvp1.setMargen(21.54);
		pvp1.setPvp(54.78);
		pvp1.setPvperId("pvpid1b");
		l1.setPvps(new ArrayList<>() {{add(pvp);add(pvp1);}});
		Linea l2 = new Linea();
		l2.setCounterLineId(new ArrayList<>() {{add("counter2");}});
		PvperLinea pvp2 = new PvperLinea();
		pvp2.setMargen(25.4);
		pvp2.setPvp(85.6);
		pvp2.setPvperId("pvpid2");
		l2.setPvps(new ArrayList<>() {{add(pvp2);}});
		lineas.add(l1);
		lineas.add(l2);
		
		var mapper = factory.from(propuesta, lineas);
		
		assertEquals(140.18, mapper.getByDupla("counter1a", sum1.getId()));
		assertEquals(140.18, mapper.getByDupla("counter1b", sum1.getId()));
		assertEquals(85.6, mapper.getByDupla("counter2", sum2.getId()));
		assertEquals(0.0, mapper.getByDupla("counter1a", sum2.getId()));
	}

}
