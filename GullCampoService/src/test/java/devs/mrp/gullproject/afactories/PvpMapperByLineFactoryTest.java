package devs.mrp.gullproject.afactories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaOferta;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.service.linea.oferta.PvpMapperByLineFactory;

class PvpMapperByLineFactoryTest {

	@Test
	void test() {
		MyFactoryFromTo<List<LineaOferta>, MyMapperByDupla<Double, String, String>> factory = new PvpMapperByLineFactory();
		
		List<LineaOferta> lineas = new ArrayList<>();
		
		LineaOferta l1 = new LineaOferta();
		l1.setCounterLineId(new ArrayList<>() {{add("p1");}});
		PvperLinea pvp = new PvperLinea();
		pvp.setMargen(1.1);
		pvp.setPvp(1.1);
		pvp.setPvperId("pvperid1");
		l1.setPvps(new ArrayList<>() {{add(pvp);}});
		
		LineaOferta l2 = new LineaOferta();
		l2.setCounterLineId(new ArrayList<>() {{add("p2");}});
		PvperLinea pvp2 = new PvperLinea();
		pvp2.setMargen(2.2);
		pvp2.setPvp(2.2);
		pvp2.setPvperId("pvperid2");
		l2.setPvps(new ArrayList<>() {{add(pvp2);}});
		
		LineaOferta l3 = new LineaOferta();
		l3.setCounterLineId(new ArrayList<>() {{add("p3");}});
		PvperLinea pvp3 = new PvperLinea();
		pvp3.setMargen(3.3);
		pvp3.setPvp(3.3);
		pvp3.setPvperId("pvperid1");
		l3.setPvps(new ArrayList<>() {{add(pvp3);}});
		
		lineas.add(l1);
		lineas.add(l2);
		lineas.add(l3);
		
		MyMapperByDupla<Double, String, String> mapper = factory.from(lineas);
		assertEquals(0.0, mapper.getByDupla("asdfasdf", "asdfasdf"));
		assertEquals(1.1, mapper.getByDupla("p1", "pvperid1"));
		assertEquals(0.0, mapper.getByDupla("p1", "pvperid2"));
		assertEquals(2.2, mapper.getByDupla("p2", "pvperid2"));
		assertEquals(3.3, mapper.getByDupla("p3", "pvperid1"));
	}

}
