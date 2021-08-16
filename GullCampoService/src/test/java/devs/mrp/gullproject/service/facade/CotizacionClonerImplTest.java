package devs.mrp.gullproject.service.facade;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.extern.slf4j.Slf4j;
import reactor.test.StepVerifier;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class CotizacionClonerImplTest {

	@Autowired ConsultaService consultaService;
	@Autowired LineaService lineaService;
	@Autowired LineaRepo lineaRepo;
	@Autowired ConsultaRepo consultaRepo;
	@Autowired CotizacionCloner cloner;
	
	@Test
	void test() {
		lineaRepo.deleteAll().block();
		consultaRepo.deleteAll().block();
		
		Consulta consulta = new Consulta();
		PropuestaCliente pc = new PropuestaCliente();
		consulta.setPropuestas(new ArrayList<>());
		consulta.getPropuestas().add(pc);
		PropuestaProveedor pp = new PropuestaProveedor();
		pp.setLineasAsignadas(2);
		pp.setNombre("nombre propuesta");
		consulta.getPropuestas().add(pp);
		
		consultaService.save(consulta).block();
		
		Linea l1 = new Linea();
		Linea l2 = new Linea();
		l1.setPropuestaId(pp.getId());
		l2.setPropuestaId(pp.getId());
		
		lineaService.addLinea(l1).block();
		lineaService.addLinea(l2).block();
		
		var lss = lineaService.findAll();
		
		log.debug("check initialization");
		StepVerifier.create(lss)
		.assertNext(l -> {
		})
		.assertNext(l -> {
		})
		.expectComplete()
		.verify();
		
		var pr = cloner.clone(pc.getId(), pp.getId()).block();
		assertEquals(pp.getNombre(), pr.getNombre());
		assertNotEquals(pp.getId(), pr.getId());
		
		var proo = consultaService.findPropuestaByPropuestaId(pp.getId());
		
		log.debug("check cloned proposal");
		StepVerifier.create(proo)
		.assertNext(p ->{
			assertEquals(2, p.getLineaIds().size());
		})
		.expectComplete()
		.verify();
		
		lss = lineaService.findByPropuestaId(pr.getId());
		
		log.debug("check cloned lines");
		StepVerifier.create(lss)
		.assertNext(l -> {
		})
		.assertNext(l -> {
		})
		.expectComplete()
		.verify();
		
		StepVerifier.create(lineaService.findAll())
		.assertNext(l->{})
		.assertNext(l->{})
		.assertNext(l->{})
		.assertNext(l->{})
		.expectComplete()
		.verify();
		
		lineaRepo.deleteAll().block();
		consultaRepo.deleteAll().block();
	}

}
