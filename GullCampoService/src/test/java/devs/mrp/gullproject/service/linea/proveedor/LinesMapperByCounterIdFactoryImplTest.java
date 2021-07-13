package devs.mrp.gullproject.service.linea.proveedor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LinesMapperByCounterIdFactoryImplTest {

	@Autowired LinesMapperByCounterIdFactory factory;
	@Autowired LineAttributeConcatenatorFactory concatenationFactory;
	
	@Test
	void test() {
		Campo<String> c1a = new Campo<>();
		c1a.setAtributoId("att1");
		c1a.setDatos("datos c1a");
		Campo<String> c1b = new Campo<>();
		c1b.setAtributoId("att2");
		c1b.setDatos("datos c1b");
		Linea l1 = new Linea();
		l1.setCampos(new ArrayList<>() {{add(c1a);add(c1b);}});
		l1.setCounterLineId(new ArrayList<>() {{add("counter1");}});
		
		Campo<String> c2a = new Campo<>();
		c2a.setAtributoId("att1");
		c2a.setDatos("datos c2a");
		Campo<String> c2b = new Campo<>();
		c2b.setAtributoId("att2");
		c2b.setDatos("datos c2b");
		Linea l2 = new Linea();
		l2.setCampos(new ArrayList<>() {{add(c2a);add(c2b);}});
		l2.setCounterLineId(new ArrayList<>() {{add("counter1");add("counter2");add("counter3");}});
		
		Campo<String> c3a = new Campo<>();
		c3a.setAtributoId("att3");
		c3a.setDatos("datos c3a");
		Linea l3 = new Linea();
		l3.setCampos(new ArrayList<>() {{add(c3a);}});
		l3.setCounterLineId(new ArrayList<>() {{add("counter3");}});
		
		
		List<Linea> lineas = new ArrayList<>() {{add(l1);add(l2);add(l3);}};
		
		var mapper = factory.from(lineas);
		
		assertEquals(l1, mapper.forCounter("counter1").get(0));
		assertEquals(l2, mapper.forCounter("counter1").get(1));
		assertEquals(2, mapper.forCounter("counter1").size());
		assertEquals(l2, mapper.forCounter("counter2").get(0));
		assertEquals(1, mapper.forCounter("counter2").size());
		assertEquals(l2, mapper.forCounter("counter3").get(0));
		assertEquals(l3, mapper.forCounter("counter3").get(1));
		assertEquals(2, mapper.forCounter("counter3").size());
		
		var concatenador = concatenationFactory.from(lineas);
		
		assertEquals("datos c1a / datos c2a", concatenador.forLineAtt("counter1", "att1"));
		assertEquals("datos c1b / datos c2b", concatenador.forLineAtt("counter1", "att2"));
		assertEquals("datos c2a", concatenador.forLineAtt("counter2", "att1"));
		assertEquals("datos c2b", concatenador.forLineAtt("counter2", "att2"));
		assertEquals("datos c2a", concatenador.forLineAtt("counter3", "att1"));
		assertEquals("datos c2b", concatenador.forLineAtt("counter3", "att2"));
		assertEquals("datos c3a", concatenador.forLineAtt("counter3", "att3"));
	}

}
