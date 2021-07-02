package devs.mrp.gullproject.service.consultaAndLineFacade;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyFactoryNew;
import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.ainterfaces.MyListMerger;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.facade.SupplierLineFinderByProposalAssignation;
import devs.mrp.gullproject.service.linea.LineFactory;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.propuesta.ProposalIdsMergerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
class LineFinderByProposalAssignationTest extends FacadeInitialization {

	ConsultaService consultaService;
	LineaService lineaService;
	MyFactoryNew<Linea> lineaFactory;
	MyFactoryFromTo<List<PropuestaProveedor>, MyListMerger<String>> mergerFactory;
	@Autowired SupplierLineFinderByProposalAssignation finder;
	
	@Autowired
	public LineFinderByProposalAssignationTest(ConsultaService consultaService, LineaService lineaService, LineFactory lineaFactory, ProposalIdsMergerFactory<PropuestaProveedor> mergerFactory) {
		super();
		this.consultaService = consultaService;
		this.lineaService = lineaService;
		this.lineaFactory = lineaFactory;
		this.mergerFactory = mergerFactory;
	}
	
	@Test
	void test() {
		Flux<Linea> lineas = finder.findBy(p1.getId());
		StepVerifier.create(lineas)
		.assertNext(l -> {
			assertEquals(l1.getId(), l.getId());
		})
		.assertNext(l -> {
			assertEquals(l2.getId(), l.getId());
		})
		.assertNext(l -> {
			assertEquals(l3.getId(), l.getId());
		})
		.expectComplete()
		.verify();
	}

}
