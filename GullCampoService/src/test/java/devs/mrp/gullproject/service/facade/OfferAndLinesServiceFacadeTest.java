package devs.mrp.gullproject.service.facade;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstractaFactory;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaOfertaRepo;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaOfferService;
import lombok.extern.slf4j.Slf4j;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class OfferAndLinesServiceFacadeTest {

	@Autowired OfferAndLinesServiceFacade facadeService;
	@Autowired LineaOfferService lineaService;
	@Autowired ConsultaService consultaService;
	@Autowired ConsultaRepo consultaRepo;
	@Autowired LineaOfertaRepo lineaRepo;
	@Autowired LineaAbstractaFactory lineaFactory;
	
	@Test
	void test() {
		consultaRepo.deleteAll().block();
		lineaRepo.deleteAll().block();
		
		Consulta consulta = new Consulta();
		Propuesta propuesta = new PropuestaNuestra();
		propuesta.setLineaIds(new ArrayList<>());
		var l1 = lineaFactory.create();
		l1.setPropuestaId(propuesta.getId());
		var l2 = lineaFactory.create();
		l2.setPropuestaId(propuesta.getId());
		List<LineaAbstracta> lineas = new ArrayList<>();
		lineas.add(l1);
		lineas.add(l2);
		
		consulta.setPropuestas(new ArrayList<>());
		consulta.getPropuestas().add(propuesta);
		
		consultaService.save(consulta).block();
		
		log.debug("first check after saving consulta");
		StepVerifier.create(consultaService.findById(consulta.getId()))
		.assertNext(rcons -> {
			assertEquals(propuesta.getId(), rcons.getPropuestas().get(0).getId());
			assertEquals(0, rcons.getPropuestas().get(0).getLineaIds().size());
		})
		.expectComplete()
		.verify();
		
		log.debug("first check should be no lines");
		StepVerifier.create(lineaService.findByPropuesta(propuesta.getId()))
		.expectComplete()
		.verify()
		;
		
		facadeService.saveAll(propuesta.getId(), lineas).blockLast();
		
		log.debug("second check offer should have the lines");
		StepVerifier.create(consultaService.findById(consulta.getId()))
		.assertNext(rcons -> {
			assertEquals(propuesta.getId(), rcons.getPropuestas().get(0).getId());
			assertEquals(2, rcons.getPropuestas().get(0).getLineaIds().size());
			assertEquals(l1.getId(), rcons.getPropuestas().get(0).getLineaIds().get(0));
			assertEquals(l2.getId(), rcons.getPropuestas().get(0).getLineaIds().get(1));
		})
		.expectComplete()
		.verify();
		
		log.debug("second check lines shall be inserted");
		StepVerifier.create(lineaService.findByPropuesta(propuesta.getId()))
		.assertNext(l -> {
			assertEquals(l1.getId(), l.getId());
		})
		.assertNext(l -> {
			assertEquals(l2.getId(), l.getId());
		})
		.expectComplete()
		.verify()
		;
		
		
		facadeService.clearAllLinesOfOffer(propuesta.getId()).block();
		
		log.debug("third check lines should be removed from the offer");
		StepVerifier.create(consultaService.findById(consulta.getId()))
		.assertNext(rcons -> {
			assertEquals(propuesta.getId(), rcons.getPropuestas().get(0).getId());
			assertEquals(0, rcons.getPropuestas().get(0).getLineaIds().size());
		})
		.expectComplete()
		.verify();
		
		log.debug("third check lines should be removed again");
		StepVerifier.create(lineaService.findByPropuesta(propuesta.getId()))
		.expectComplete()
		.verify()
		;
		
		consultaRepo.deleteAll().block();
		lineaRepo.deleteAll().block();
	}

}
