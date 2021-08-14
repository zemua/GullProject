package devs.mrp.gullproject.service.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.linea.LineaService;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ConsultaAndLinesFacadeTest {

	@Autowired ConsultaAndLinesFacade consultaAndLinesFacade;
	@Autowired LineaService lineaService;
	@Autowired LineaRepo lineaRepo;
	@Autowired ConsultaRepo consultaRepo;
	
	@BeforeEach
	void setup() {
		lineaRepo.deleteAll().block();
		consultaRepo.deleteAll().block();
	}
	
	@AfterEach
	void clear() {
		lineaRepo.deleteAll().block();
		consultaRepo.deleteAll().block();
	}
	
	@Test
	void testUpdateAssignedLinesOfProposal() {
		Consulta consulta = new Consulta();
		PropuestaProveedor prop = new PropuestaProveedor();
		consulta.setPropuestas(List.of(prop));
		
		Linea l1 = new Linea();
		Linea l2 = new Linea();
		Linea l3 = new Linea();
		l1.setPropuestaId(prop.getId());
		l1.setCounterLineId(List.of("uno", "dos", "tres"));
		l2.setPropuestaId(prop.getId());
		l2.setCounterLineId(List.of("cuatro"));
		l3.setPropuestaId(prop.getId());
		
		lineaService.addLinea(l1).block();
		lineaService.addLinea(l2).block();
		lineaService.addLinea(l3).block();
		
		consultaRepo.save(consulta).block();
		
		StepVerifier.create(consultaRepo.findById(consulta.getId()))
		.assertNext(rCons -> {
			assertEquals(prop.getId(), ((PropuestaProveedor)rCons.getPropuestas().get(0)).getId());
			assertEquals(0, ((PropuestaProveedor)rCons.getPropuestas().get(0)).getLineasAsignadas());
		})
		.expectComplete()
		.verify();
		
		consultaAndLinesFacade.updateAssignedLinesOfProposal(prop.getId()).block();
		
		StepVerifier.create(consultaRepo.findById(consulta.getId()))
		.assertNext(rCons -> {
			assertEquals(prop.getId(), ((PropuestaProveedor)rCons.getPropuestas().get(0)).getId());
			assertEquals(4, ((PropuestaProveedor)rCons.getPropuestas().get(0)).getLineasAsignadas());
		})
		.expectComplete()
		.verify();
	}

}
