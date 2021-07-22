package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.service.linea.LineByAssignationRetriever;
import devs.mrp.gullproject.service.linea.oferta.PvpMapperByAssignedLine;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import({LineaFactory.class, Consulta.class})
class PvpMapperByAssignedLineTest {
	
	@Autowired LineaFactory lineaFactory;
	
	@Test
	void test() {
		List<Linea> lineas = new ArrayList<>();
		
		Linea l1 = lineaFactory.create();
		l1.setCounterLineId(new ArrayList<>() {{add("p1");}});
		PvperLinea pvp = new PvperLinea();
		pvp.setMargen(1.1);
		pvp.setPvp(1.1);
		pvp.setPvperId("pvperid1");
		l1.setPvps(new ArrayList<>() {{add(pvp);}});
		
		Linea l2 = lineaFactory.create();
		l2.setCounterLineId(new ArrayList<>() {{add("p2");}});
		PvperLinea pvp2 = new PvperLinea();
		pvp2.setMargen(2.2);
		pvp2.setPvp(2.2);
		pvp2.setPvperId("pvperid2");
		l2.setPvps(new ArrayList<>() {{add(pvp2);}});
		
		Linea l3 = lineaFactory.create();
		l3.setCounterLineId(new ArrayList<>() {{add("p3");}});
		PvperLinea pvp3 = new PvperLinea();
		pvp3.setMargen(3.3);
		pvp3.setPvp(3.3);
		pvp3.setPvperId("pvperid1");
		l3.setPvps(new ArrayList<>() {{add(pvp3);}});
		
		lineas.add(l1);
		lineas.add(l2);
		lineas.add(l3);
		
		MyListOfAsignables<Linea> asignables = new LineByAssignationRetriever(lineas);
		MyMapperByDupla<Double, String, String> mapper = new PvpMapperByAssignedLine(asignables);
		
		assertEquals(0.0, mapper.getByDupla("asdfasdf", "asdfasdf"));
		assertEquals(1.1, mapper.getByDupla("p1", "pvperid1"));
		assertEquals(0.0, mapper.getByDupla("p1", "pvperid2"));
		assertEquals(2.2, mapper.getByDupla("p2", "pvperid2"));
		assertEquals(3.3, mapper.getByDupla("p3", "pvperid1"));
	}

}
