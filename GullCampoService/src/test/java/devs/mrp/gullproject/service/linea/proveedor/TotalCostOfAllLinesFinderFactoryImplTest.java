package devs.mrp.gullproject.service.linea.proveedor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.service.propuesta.proveedor.ProposalCostNameMapperFromPvpFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TotalCostOfAllLinesFinderFactoryImplTest {

	@Autowired TotalCostOfAllLinesFinderFactory totalCostFactory;
	@Autowired ProposalCostNameMapperFromPvpFactory costFromPvpMapper;
	@Autowired CostMapperByIdFactory lineCostByCostIdMapper;
	
	@Test
	void test() {
		PropuestaProveedor prop1 = new PropuestaProveedor();
		CosteProveedor cost1 = new CosteProveedor();
		cost1.setName("namecost1");
		CosteProveedor cost2 = new CosteProveedor();
		cost2.setName("namecost2");
		prop1.setCostes(new ArrayList<>() {{add(cost1);add(cost2);}});
		PropuestaProveedor prop2 = new PropuestaProveedor();
		CosteProveedor cost3 = new CosteProveedor();
		cost3.setName("namecost3");
		prop2.setCostes(new ArrayList<>() {{add(cost3);}});
		List<PropuestaProveedor> propuestas = new ArrayList<>() {{add(prop1);add(prop2);}};
		
		PropuestaNuestra oferta = new PropuestaNuestra();
		Pvper pvp1 = new Pvper();
		pvp1.setIdCostes(new ArrayList<>() {{add(cost1.getId());add(cost2.getId());}});
		pvp1.setName("pvp1name");
		Pvper pvp2 = new Pvper();
		pvp2.setIdCostes(new ArrayList<>() {{add(cost2.getId());add(cost3.getId());}});
		oferta.setPvps(new ArrayList<>() {{add(pvp1);add(pvp2);}});
		
		var nameMapper = costFromPvpMapper.from(oferta, propuestas);
		
		CosteLineaProveedor c1 = new CosteLineaProveedor();
		c1.setCosteProveedorId(cost1.getId());
		c1.setValue(5);
		CosteLineaProveedor c2 = new CosteLineaProveedor();
		c2.setCosteProveedorId(cost2.getId());
		c2.setValue(6);
		CosteLineaProveedor c3 = new CosteLineaProveedor();
		c3.setCosteProveedorId(cost3.getId());
		c3.setValue(7);
		
		List<Linea> lineas = new ArrayList<>();
		Linea l1 = new Linea();
		l1.setCounterLineId(new ArrayList<>() {{add("counter1");}});
		l1.setCostesProveedor(new ArrayList<>() {{add(c1);add(c2);}});
		Linea l2 = new Linea();
		l2.setCounterLineId(new ArrayList<>() {{add("counter2");}});
		l2.setCostesProveedor(new ArrayList<>() {{add(c2);add(c3);}});
		lineas.add(l1);
		lineas.add(l2);
		
		var lineCostMapper = lineCostByCostIdMapper.from(lineas);
		
		List<String> counters = new ArrayList<>() {{add("counter1");add("counter2");}};
		
		var totalCostfinder = totalCostFactory.from(nameMapper, lineCostMapper, counters);
		
		assertEquals(17, totalCostfinder.forPvp(pvp1.getId()));
		assertEquals(19, totalCostfinder.forPvp(pvp2.getId()));
	}

}
