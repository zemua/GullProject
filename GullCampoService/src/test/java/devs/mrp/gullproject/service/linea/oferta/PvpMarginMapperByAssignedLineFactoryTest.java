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
import devs.mrp.gullproject.domains.propuestas.Pvper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PvpMarginMapperByAssignedLineFactoryTest {

	@Autowired PvpMarginMapperByAssignedLineFactory factory;
	
	@Test
	void test() {
		List<Linea> lineas = new ArrayList<>();
		Linea l1 = new Linea();
		l1.setCounterLineId(new ArrayList<>() {{add("counter1a");add("counter1b");}});
		PvperLinea pvp = new PvperLinea();
		pvp.setMargen(25.6);
		pvp.setPvp(85.4);
		pvp.setPvperId("pvpid1");
		l1.setPvps(new ArrayList<>() {{add(pvp);}});
		Linea l2 = new Linea();
		l2.setCounterLineId(new ArrayList<>() {{add("counter2");}});
		PvperLinea pvp2 = new PvperLinea();
		pvp2.setMargen(25.4);
		pvp2.setPvp(85.6);
		pvp2.setPvperId("pvpid2");
		l2.setPvps(new ArrayList<>() {{add(pvp2);}});
		lineas.add(l1);
		lineas.add(l2);
		
		var mapper = factory.from(lineas);
		
		assertEquals(25.6, mapper.getByDupla("counter1a", pvp.getPvperId()));
		assertEquals(25.6, mapper.getByDupla("counter1b", pvp.getPvperId()));
		assertEquals(25.4, mapper.getByDupla("counter2", pvp2.getPvperId()));
		assertEquals(0.0, mapper.getByDupla("counter1a", pvp2.getPvperId()));
	}

}
