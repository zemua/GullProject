package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import devs.mrp.gullproject.ainterfaces.MyListMerger;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.service.propuesta.ProposalIdsMerger;

class ProposalIdsMergerTest {

	@Test
	void test() {
		List<Propuesta> propuestas = new ArrayList<>();
		var p1 = new PropuestaProveedor();
		var p2 = new PropuestaNuestra();
		var p3 = new PropuestaCliente();
		propuestas.add(p1);
		propuestas.add(p2);
		propuestas.add(p3);
		
		MyListMerger<String> merger = new ProposalIdsMerger(propuestas);
		List<String> merged = merger.merge();
		
		assertEquals(p1.getId(), merged.get(0));
		assertEquals(p2.getId(), merged.get(1));
		assertEquals(p3.getId(), merged.get(2));
	}

}
