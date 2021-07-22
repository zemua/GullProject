package devs.mrp.gullproject.domainsdto.propuesta.oferta;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckboxedCosts.AttsList;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckboxedCosts.CheckboxedAttId;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckboxedCosts.CheckboxedCostId;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PvperCheckboxedCostToPvperTest {

	@Autowired PvperCheckboxedCostToPvper converter;
	
	@Test
	void test() {
		PvperCheckboxedCosts dto = new PvperCheckboxedCosts();
		dto.setName("setted name");
		CheckboxedCostId cost1 = new CheckboxedCostId();
		cost1.setId("cost1id");
		cost1.setSelected(true);
		CheckboxedCostId cost2 = new CheckboxedCostId();
		cost2.setId("cost2id");
		cost2.setSelected(false);
		dto.setCosts(new ArrayList<>() {{add(cost1);add(cost2);}});
		
		CheckboxedAttId attid1 = new CheckboxedAttId();
		attid1.setId("attid1");
		attid1.setSelected(true);
		CheckboxedAttId attid2 = new CheckboxedAttId();
		attid2.setId("attid2");
		attid2.setSelected(false);
		AttsList atts1 = new AttsList();
		atts1.setCotizId("cotiz1id");
		atts1.setAtts(new ArrayList<>() {{add(attid1);add(attid2);}});
		dto.setAttributesByCotiz(new ArrayList<>() {{add(atts1);}});
		
		var pvper = converter.from(dto);
		
		assertEquals(dto.getName(), pvper.getName());
		assertEquals(1, pvper.getIdCostes().size());
		assertEquals(cost1.getId(), pvper.getIdCostes().get(0));
		assertEquals(1, pvper.getIdAttributesByCotiz().size());
		assertEquals(atts1.getCotizId(), pvper.getIdAttributesByCotiz().get(0).getCotizId());
		assertEquals(1, pvper.getIdAttributesByCotiz().get(0).getIds().size());
		assertEquals(attid1.getId(), pvper.getIdAttributesByCotiz().get(0).getIds().get(0));
	}

}
