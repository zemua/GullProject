package devs.mrp.gullproject.service.linea.oferta.selectable;

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
import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLineFactory;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLinesWrapFactory;
import devs.mrp.gullproject.service.linea.proveedor.LineAttributeConcatenatorFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LineReConstructorImplTest {

	@Autowired LineReConstructor reconstructor;
	@Autowired SelectableLineFactory selectableFactory;
	@Autowired SelectableLinesWrapFactory wrapFactory;
	@Autowired LineAttributeConcatenatorFactory concatenatorFactory;
	
	@Test
	void test() {
		SelectableAbstractLine l1 = selectableFactory.create();
		l1.setCounterLineId("counter1");
		l1.setSelected(true);
		SelectableAbstractLine l2 = selectableFactory.create();
		l2.setCounterLineId("counter2");
		l2.setSelected(true);
		SelectableAbstractLine l3 = selectableFactory.create();
		l3.setCounterLineId("counter1");
		l3.setSelected(true);
		SelectableAbstractLine l4 = selectableFactory.create();
		l4.setCounterLineId("counter2");
		l4.setSelected(false);
		
		var wrap = wrapFactory.create();
		wrap.setLineas(new ArrayList<>() {{add(l1);add(l2);add(l3);add(l4);}});
		
		AtributoForCampo att1 = new AtributoForCampo();
		att1.setId("att1id");
		AtributoForCampo att2 = new AtributoForCampo();
		att2.setId("att2id");
		
		List<AtributoForCampo> atributos = new ArrayList<>() {{add(att1);add(att2);}};
		
		Campo<String> c1a = new Campo<>();
		c1a.setAtributoId("att1id");
		c1a.setDatos("datos c1a");
		Campo<String> c1b = new Campo<>();
		c1b.setAtributoId("att2id");
		c1b.setDatos("datos c1b");
		Linea lp1 = new Linea();
		lp1.setCounterLineId(new ArrayList<>() {{add("counter1");}});
		lp1.setCampos(new ArrayList<>() {{add(c1a);add(c1b);}});
		
		Campo<String> c2a = new Campo<>();
		c2a.setAtributoId("att1id");
		c2a.setDatos("datos c2a");
		Linea lp2 = new Linea();
		lp2.setCounterLineId(new ArrayList<>() {{add("counter1");add("counter2");}});
		lp2.setCampos(new ArrayList<>() {{add(c2a);}});
		
		Campo<String> c3b = new Campo<>();
		c3b.setAtributoId("att2id");
		c3b.setDatos("datos c3b");
		Linea lp3 = new Linea();
		lp3.setCounterLineId(new ArrayList<>() {{add("counter2");}});
		lp3.setCampos(new ArrayList<>() {{add(c3b);}});
		
		List<Linea> lineas = new ArrayList<>() {{add(lp1);add(lp2);add(lp3);}};
		
		var concatenator = concatenatorFactory.from(lineas);
		
		List<LineaAbstracta> abstractas = reconstructor.from(wrap, concatenator, atributos, "random");
		
		assertEquals(3, abstractas.size());
		assertEquals(l1.getId(), abstractas.get(0).getId());
		assertEquals("datos c1a / datos c2a", abstractas.get(0).getCampos().get(0).getDatosText());
		assertEquals("datos c1b", abstractas.get(0).getCampos().get(1).getDatosText());
		assertEquals("datos c2a", abstractas.get(1).getCampos().get(0).getDatosText());
		assertEquals("datos c3b", abstractas.get(1).getCampos().get(1).getDatosText());
		assertEquals("datos c1a / datos c2a", abstractas.get(2).getCampos().get(0).getDatosText());
		assertEquals("datos c1b", abstractas.get(2).getCampos().get(1).getDatosText());
	}

}
