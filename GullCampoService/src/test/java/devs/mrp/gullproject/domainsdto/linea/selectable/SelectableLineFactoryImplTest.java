package devs.mrp.gullproject.domainsdto.linea.selectable;

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
class SelectableLineFactoryImplTest {

	@Autowired LineaAbstractaFactory lineaFactory;
	@Autowired SelectableLineFactory selectableFactory;
	
	@Test
	void test() {
		List<LineaAbstracta> lineas = new ArrayList<>();
		lineas.add(lineaFactory.create());
		lineas.add(lineaFactory.create());
		lineas.add(lineaFactory.create());
		
		var lista = selectableFactory.from(lineas);
		
		assertEquals(lineas.get(0).getId(), lista.get(0).getId());
		assertEquals(lineas.get(1).getId(), lista.get(1).getId());
		assertEquals(lineas.get(2).getId(), lista.get(2).getId());
	}

}
