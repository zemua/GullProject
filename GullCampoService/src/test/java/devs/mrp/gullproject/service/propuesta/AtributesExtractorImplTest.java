package devs.mrp.gullproject.service.propuesta;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AtributesExtractorImplTest {

	@Autowired AttributesExtractor extractor;
	
	@Test
	void test() {
		AtributoForCampo att1 = new AtributoForCampo();
		att1.setId("att1");
		AtributoForCampo att2 = new AtributoForCampo();
		att2.setId("att2");
		AtributoForCampo att3 = new AtributoForCampo();
		att3.setId("att3");
		
		Propuesta prop1 = new PropuestaProveedor();
		prop1.setAttributeColumns(new ArrayList<>() {{add(att1);add(att2);}});
		Propuesta prop2 = new PropuestaProveedor();
		prop2.setAttributeColumns(new ArrayList<>() {{add(att2);add(att3);}});
		
		var atts = extractor.fromProposals(new ArrayList<>() {{add(prop1);add(prop2);}});
		
		assertEquals(3, atts.size());
		assertEquals(att1, atts.get(0));
		assertEquals(att2, atts.get(1));
		assertEquals(att3, atts.get(2));
	}

}
