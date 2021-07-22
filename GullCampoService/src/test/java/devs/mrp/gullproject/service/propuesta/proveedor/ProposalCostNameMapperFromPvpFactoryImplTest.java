package devs.mrp.gullproject.service.propuesta.proveedor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.Pvper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProposalCostNameMapperFromPvpFactoryImplTest {

	@Autowired ProposalCostNameMapperFromPvpFactory factory;
	
	@Test
	void test() {
		PropuestaProveedor propuesta1 = new PropuestaProveedor();
		CosteProveedor cost1a = new CosteProveedor();
		cost1a.setName("name1a");
		CosteProveedor cost1b = new CosteProveedor();
		cost1b.setName("name1b");
		propuesta1.setCostes(new ArrayList<>() {{add(cost1a);add(cost1b);}});
		PropuestaProveedor propuesta2 = new PropuestaProveedor();
		CosteProveedor cost2 = new CosteProveedor();
		cost1a.setName("name2");
		propuesta2.setCostes(new ArrayList<>() {{add(cost2);}});
		
		List<PropuestaProveedor> propuestas = new ArrayList<>();
		propuestas.add(propuesta1);
		propuestas.add(propuesta2);
		
		PropuestaNuestra oferta = new PropuestaNuestra();
		Pvper pvp1 = new Pvper();
		pvp1.setName("pvp1");
		pvp1.setIdCostes(new ArrayList<>() {{add(cost1a.getId());add(cost1b.getId());}});
		Pvper pvp2 = new Pvper();
		pvp2.setName("pvp2");
		pvp2.setIdCostes(new ArrayList<>() {{add(cost1a.getId());add(cost2.getId());}});
		Pvper pvp3 = new Pvper();
		pvp3.setName("pvp3");
		pvp3.setIdCostes(new ArrayList<>() {{add(cost1b.getId());}});
		oferta.setPvps(new ArrayList<>() {{add(pvp1);add(pvp2);add(pvp3);}});
		
		var mapper = factory.from(oferta, propuestas);
		
		assertEquals(2, mapper.findBy(pvp1.getId()).size());
		assertEquals(cost1a.getId(), mapper.findBy(pvp1.getId()).get(0).getId());
		assertEquals(cost1b.getId(), mapper.findBy(pvp1.getId()).get(1).getId());
		assertEquals(2, mapper.findBy(pvp2.getId()).size());
		assertEquals(cost1a.getId(), mapper.findBy(pvp2.getId()).get(0).getId());
		assertEquals(cost2.getId(), mapper.findBy(pvp2.getId()).get(1).getId());
		assertEquals(1, mapper.findBy(pvp3.getId()).size());
		assertEquals(cost1b.getId(), mapper.findBy(pvp3.getId()).get(0).getId());
	}

}
