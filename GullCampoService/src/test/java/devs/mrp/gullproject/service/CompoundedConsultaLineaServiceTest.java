package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CompoundedConsultaLineaServiceTest {

	CompoundedConsultaLineaService service;
	LineaRepo lineaRepo;
	ConsultaRepo consultaRepo;
	
	@Autowired
	CompoundedConsultaLineaServiceTest(CompoundedConsultaLineaService service, LineaRepo lineaRepo, ConsultaRepo consultaRepo) {
		this.service = service;
		this.lineaRepo = lineaRepo;
		this.consultaRepo = consultaRepo;
	}
	
	Consulta consulta;
	PropuestaCliente p1;
	PropuestaProveedor pp1;
	PropuestaProveedor pp2;
	PropuestaNuestra pn1;
	PropuestaNuestra pn2;
	Linea l1;
	Linea l2;
	Linea l3;
	Linea l4;
	Linea l5;
	Linea l6;
	
	Mono<Consulta> monoConsulta;
	Flux<Linea> fluxLineas;
	
	@BeforeEach
	void init() {
		consultaRepo.deleteAll().block();
		lineaRepo.deleteAll().block();
		
		p1 = new PropuestaCliente();
		
		pp1 = new PropuestaProveedor(); pp1.setForProposalId(p1.getId());
		pp2 = new PropuestaProveedor(); pp2.setForProposalId(p1.getId());
		pn1 = new PropuestaNuestra(); pn1.setForProposalId(p1.getId());
		pn2 = new PropuestaNuestra(); pn2.setForProposalId(p1.getId());
		
		consulta = new Consulta();
		p1.setForProposalId(consulta.getId());
		consulta.getPropuestas().add(p1);
		consulta.getPropuestas().add(pp1);
		consulta.getPropuestas().add(pp2);
		consulta.getPropuestas().add(pn1);
		consulta.getPropuestas().add(pn2);
		
		consultaRepo.save(consulta).block();
		
		l1 = new Linea(); l1.setPropuestaId(pp1.getId()); l1.setOrder(1); l1.setNombre("nombre linea 1");
		l2 = new Linea(); l2.setPropuestaId(pp1.getId()); l2.setOrder(2); l2.setNombre("nombre linea 2");
		l3 = new Linea(); l3.setPropuestaId(pp2.getId()); l3.setOrder(3); l3.setNombre("nombre linea 3");
		l4 = new Linea(); l4.setPropuestaId(pn1.getId()); l4.setOrder(4); l4.setNombre("nombre linea 4");
		l5 = new Linea(); l5.setPropuestaId(pn2.getId()); l5.setOrder(5); l5.setNombre("nombre linea 5");
		l6 = new Linea(); l6.setPropuestaId(p1.getId()); l6.setOrder(6); l6.setNombre("nombre linea 6");
		
		lineaRepo.save(l1).block();
		lineaRepo.save(l2).block();
		lineaRepo.save(l3).block();
		lineaRepo.save(l4).block();
		lineaRepo.save(l5).block();
		lineaRepo.save(l6).block();
		
		monoConsulta = consultaRepo.findById(consulta.getId());
		fluxLineas = lineaRepo.findAllByOrderByOrderAsc();
		
		StepVerifier.create(monoConsulta)
		.assertNext(cons -> {
			assertEquals(5, cons.operations().getCantidadPropuestas());
		})
		.expectComplete()
		.verify()
		;
		
		log.debug("id de l1 " + l1.getId() + " y propuesta Id es " + l1.getPropuestaId());
		log.debug("id de l2 " + l2.getId() + " y propuesta Id es " + l2.getPropuestaId());
		log.debug("id de l3 " + l3.getId() + " y propuesta Id es " + l3.getPropuestaId());
		log.debug("id de l4 " + l4.getId() + " y propuesta Id es " + l4.getPropuestaId());
		log.debug("id de l5 " + l5.getId() + " y propuesta Id es " + l5.getPropuestaId());
		log.debug("id de l6 " + l6.getId() + " y propuesta Id es " + l6.getPropuestaId());
		
		StepVerifier.create(fluxLineas)
		.assertNext(l -> {
			assertEquals(l1.getNombre(), l.getNombre());
			assertEquals(l1.getId(), l.getId());
		})
		.assertNext(l -> {
			assertEquals(l2.getId(), l.getId());
		})
		.assertNext(l -> {
			assertEquals(l3.getId(), l.getId());
		})
		.assertNext(l -> {
			assertEquals(l4.getId(), l.getId());
		})
		.assertNext(l -> {
			assertEquals(l5.getId(), l.getId());
		})
		.assertNext(l -> {
			assertEquals(l6.getId(), l.getId());
		})
		.expectComplete()
		.verify()
		;
	}
	
	@AfterEach
	void clear() {
		lineaRepo.deleteAll().block();
		consultaRepo.deleteAll().block();
	}
	
	@Test
	void testRemovePropuestasAssignedToAndTheirLines() {
		service.removePropuestasAssignedToAndTheirLines(consulta.getId(), p1.getId()).block();
		
		StepVerifier.create(monoConsulta)
		.assertNext(cons -> {
			assertEquals(1, cons.operations().getCantidadPropuestas());
			assertEquals(p1.getId(), cons.getPropuestas().get(0).getId());
		})
		.expectComplete()
		.verify()
		;
		
		StepVerifier.create(fluxLineas)
		.assertNext(l -> {
			assertEquals(l6.getId(), l.getId());
		})
		.expectComplete()
		.verify()
		;
	}
	
}
