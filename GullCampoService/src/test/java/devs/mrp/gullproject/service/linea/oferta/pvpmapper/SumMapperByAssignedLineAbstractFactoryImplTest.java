package devs.mrp.gullproject.service.linea.oferta.pvpmapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstractaFactory;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PvperSum;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SumMapperByAssignedLineAbstractFactoryImplTest {

	@Autowired SumMapperByAssignedLineAbstractFactory mapperFactory;
	@Autowired LineaAbstractaFactory lineaFactory;
	
	@Test
	void test() {
		PropuestaNuestra propuesta = new PropuestaNuestra();
		PvperSum sum1 = new PvperSum();
		sum1.setName("sum1name");
		sum1.setPvperIds(new ArrayList<>() {{add("pvpid1");add("pvpid2");}});
		PvperSum sum2 = new PvperSum();
		sum2.setName("sum2name");
		sum2.setPvperIds(new ArrayList<>() {{add("pvpid2");add("pvpid3");}});
		propuesta.setSums(new ArrayList<>() {{add(sum1);add(sum2);}});
		
		PvperLinea pvp1 = new PvperLinea();
		pvp1.setMargen(25);
		pvp1.setPvp(26);
		pvp1.setPvperId("pvpid1");
		
		PvperLinea pvp2 = new PvperLinea();
		pvp2.setMargen(15);
		pvp2.setPvp(16);
		pvp2.setPvperId("pvpid1");
		
		PvperLinea pvp3 = new PvperLinea();
		pvp3.setMargen(5);
		pvp3.setPvp(6);
		pvp3.setPvperId("pvpid2");
		
		PvperLinea pvp4 = new PvperLinea();
		pvp4.setMargen(7);
		pvp4.setPvp(8);
		pvp4.setPvperId("pvpid2");
		
		List<LineaAbstracta> lineas = new ArrayList<>();
		
		var l1 = lineaFactory.create();
		l1.setCounterLineId("counter1");
		l1.setPvp(pvp1);
		
		var l2 = lineaFactory.create();
		l2.setCounterLineId("counter2");
		l2.setPvp(pvp2);
		
		var l3 = lineaFactory.create();
		l3.setCounterLineId("counter1");
		l3.setPvp(pvp3);
		
		var l4 = lineaFactory.create();
		l4.setCounterLineId("counter2");
		l4.setPvp(pvp4);
		
		lineas.add(l1);
		lineas.add(l2);
		lineas.add(l3);
		lineas.add(l4);
		
		var mapper = mapperFactory.from(propuesta, lineas);
		
		assertEquals(32, mapper.findBy("counter1", sum1.getId()));
		assertEquals(6, mapper.findBy("counter1", sum2.getId()));
		assertEquals(24, mapper.findBy("counter2", sum1.getId()));
		assertEquals(8, mapper.findBy("counter2", sum2.getId()));
	}

}
