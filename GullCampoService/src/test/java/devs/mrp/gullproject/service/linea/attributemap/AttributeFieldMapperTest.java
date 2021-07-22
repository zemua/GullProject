package devs.mrp.gullproject.service.linea.attributemap;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstractaFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AttributeFieldMapperTest {

	@Autowired AttributeFieldMapperFactory mapperFactory;
	@Autowired LineaAbstractaFactory lineaFactory;
	
	@Test
	void test() {
		var l1 = lineaFactory.create();
		var l2 = lineaFactory.create();
		var l3 = lineaFactory.create();
		
		l1.setCounterLineId("counterA");
		l2.setCounterLineId("counterA");
		l3.setCounterLineId("counterB");
		
		PvperLinea pvp1 = new PvperLinea(); pvp1.setMargen(12); pvp1.setPvp(23); pvp1.setPvperId("pvperid1");
		PvperLinea pvp2 = new PvperLinea(); pvp2.setMargen(21); pvp2.setPvp(32); pvp2.setPvperId("pvperid2");
		
		l1.setPvp(pvp1);
		l2.setPvp(pvp2);
		l3.setPvp(pvp1);
		
		Campo<String> campo1 = new Campo<>(); campo1.setDatos("campo1datos"); campo1.setAtributoId("att1id");
		Campo<String> campo2 = new Campo<>(); campo2.setDatos("campo2datos"); campo2.setAtributoId("att2id");
		Campo<String> campo3 = new Campo<>(); campo3.setDatos("campo3datos"); campo3.setAtributoId("att1id");
		Campo<String> campo4 = new Campo<>(); campo4.setDatos("campo4datos"); campo4.setAtributoId("att2id");
		
		l1.setCampos(List.of(campo1, campo2));
		l2.setCampos(List.of(campo3, campo2));
		l3.setCampos(List.of(campo1, campo4));
		
		var mapper = mapperFactory.from(List.of(l1, l2, l3));
		
		assertEquals("campo1datos", mapper.of("counterA", pvp1.getPvperId(), "att1id"));
		assertEquals("campo2datos", mapper.of("counterA", pvp1.getPvperId(), "att2id"));
		assertEquals("campo3datos", mapper.of("counterA", pvp2.getPvperId(), "att1id"));
		assertEquals("campo2datos", mapper.of("counterA", pvp2.getPvperId(), "att2id"));
		assertEquals("campo1datos", mapper.of("counterB", pvp1.getPvperId(), "att1id"));
		assertEquals("campo4datos", mapper.of("counterB", pvp1.getPvperId(), "att2id"));
	}

}
