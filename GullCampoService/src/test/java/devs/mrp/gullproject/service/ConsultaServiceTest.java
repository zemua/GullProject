package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import devs.mrp.gullproject.domains.Pvper;
import devs.mrp.gullproject.domains.dto.CostesCheckbox;
import devs.mrp.gullproject.domains.dto.CostesCheckboxWrapper;
import devs.mrp.gullproject.domains.dto.PvperCheckbox;
import devs.mrp.gullproject.domains.dto.PvpsCheckboxWrapper;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.CustomConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ConsultaServiceTest {

	ConsultaService consultaService;
	ConsultaRepo consultaRepo;
	LineaRepo lineaRepo;
	ModelMapper modelMapper;
	
	@Autowired
	public ConsultaServiceTest(ConsultaService consultaService, ConsultaRepo consultaRepo, ModelMapper modelMapper, LineaRepo lineaRepo) {
		this.consultaService = consultaService;
		this.consultaRepo = consultaRepo;
		if (!(consultaRepo instanceof CustomConsultaRepo)) {
			fail("ConsultaRepo no extiende CustomConsultaRepo");
		}
		this.modelMapper = modelMapper;
		this.lineaRepo = lineaRepo;
	}
	
	Consulta consulta;
	Propuesta propuesta1;
	Propuesta propuesta2;
	PropuestaProveedor propuestaProveedor;
	PropuestaNuestra propuestaNuestra;
	Mono<Consulta> mono;
	AtributoForCampo att1;
	AtributoForCampo att2;
	CosteProveedor cost1;
	CosteProveedor cost2;
	Pvper pvp1;
	Pvper pvp2;
	
	@BeforeEach
	void init() {
		consultaRepo.deleteAll().block();
		lineaRepo.deleteAll().block();
		
		consulta = new Consulta();
		propuesta1 = new PropuestaCliente();
		propuesta2 = new PropuestaCliente();
		
		att1 = new AtributoForCampo();
		att1.setId("id1");
		att1.setName("name1");
		att1.setTipo("tipo1");
		
		att2 = new AtributoForCampo();
		att2.setId("id2");
		att2.setName("name2");
		att2.setTipo("tipo2");
		
		propuesta1.operations().addAttribute(att1);
		propuesta1.setNombre("nombre original");
		propuesta1.operations().addAttribute(att2);
		
		consulta.operations().addPropuesta(propuesta1);
		consulta.operations().addPropuesta(propuesta2);
		
		consultaRepo.save(consulta).block();
		
		mono = consultaRepo.findById(consulta.getId());
		
		StepVerifier.create(mono)
			.assertNext(cons -> {
				assertEquals(2, cons.operations().getCantidadPropuestas());
				assertEquals(2, cons.operations().getPropuestaByIndex(0).getAttributeColumns().size());
			})
			.expectComplete()
			.verify();
	}
	
	@AfterEach
	void clear() {
		consultaRepo.deleteAll().block();
		lineaRepo.deleteAll().block();
	}
	
	private void addCosts() {
		propuestaProveedor = new PropuestaProveedor();
		log.debug("new propuesta: " + propuestaProveedor.toString());
		propuestaProveedor.setForProposalId(propuesta1.getId());
		log.debug("with for proposal id: " + propuestaProveedor.toString());
		propuestaProveedor.operations().addAttribute(att1);
		log.debug("with att1: " + propuestaProveedor.toString());
		propuestaProveedor.setNombre("nombre propuesta proveedor");
		log.debug("with nombre: " + propuestaProveedor.toString());
		propuestaProveedor.operations().addAttribute(att2);
		log.debug("with att2: " + propuestaProveedor.toString());
		cost1 = new CosteProveedor();
		cost1.setName("coste 1");
		cost2 = new CosteProveedor();
		cost2.setName("coste 2");
		List<CosteProveedor> costs = new ArrayList<>();
		costs.add(cost1);
		costs.add(cost2);
		((PropuestaProveedor)propuestaProveedor).setCostes(costs);
		log.debug("with costs: " + propuestaProveedor.toString());
		consultaService.addPropuesta(consulta.getId(), propuestaProveedor).block();
		
		propuestaNuestra = new PropuestaNuestra();
		propuestaNuestra.setForProposalId(propuesta1.getId());
		propuestaNuestra.operations().addAttribute(att1);
		propuestaNuestra.setNombre("nombre propuesta nuestra");
		propuestaNuestra.operations().addAttribute(att2);
		pvp1 = new Pvper();
		pvp1.setIdCostes(new ArrayList<>() {{add(cost1.getId());}});
		pvp1.setName("pvp1 name");
		pvp2 = new Pvper();
		pvp2.setIdCostes(new ArrayList<>() {{add(cost2.getId());}});
		List<Pvper> pvps = new ArrayList<>();
		pvps.add(pvp1);
		pvps.add(pvp2);
		((PropuestaNuestra)propuestaNuestra).setPvps(pvps);
		consultaService.addPropuesta(consulta.getId(), propuestaNuestra).block();
	}
	
	@Test
	void testRemovePropuestaById() {
		
		Integer cant = consultaService.removePropuestaById(consulta.getId(), propuesta1.getId()).block();
		
		StepVerifier.create(mono)
		.assertNext(cons -> {
			assertEquals(1, cons.operations().getCantidadPropuestas());
			assertEquals(propuesta2.getId(), cons.operations().getPropuestaByIndex(0).getId());;
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testFindPropuestaByPropuestaId() {
		
		Mono<Propuesta> mono = consultaService.findPropuestaByPropuestaId(propuesta1.getId());
		
		StepVerifier.create(mono)
		.assertNext(prop -> {
			assertEquals(propuesta1, prop);
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testFindAttributesByPropuestaId() {
		
		Flux<AtributoForCampo> flux = consultaService.findAttributesByPropuestaId(propuesta1.getId());
		
		StepVerifier.create(flux)
		.assertNext(att -> {
			assertEquals(att, att1);
		})
		.assertNext(att -> {
			assertEquals(att, att2);
		})
		.expectComplete()
		.verify();
		
	}
	
	@Test
	void testUpdateNameAndStatus() {
		Consulta nCons = consultaService.updateNameAndStatus(consulta.getId(), "nuevo nombre", "nuevo status").block();
		
		StepVerifier.create(mono)
		.assertNext(oCons -> {
			assertEquals("nuevo nombre", oCons.getNombre());
			assertEquals("nuevo status", oCons.getStatus());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateNombrePropuesta() {
		propuesta1.setNombre("nombre actualizado");
		Consulta nCons = consultaService.updateNombrePropuesta(propuesta1).block();
		
		StepVerifier.create(mono)
		.assertNext(oCons -> {
			assertEquals("nombre actualizado", oCons.operations().getPropuestaByIndex(0).getNombre());
		})
		.expectComplete()
		.verify();
	}
	
	@Test
	void testFindAllPropuestasOfConsulta() {
		Flux<Propuesta> props  = consultaService.findAllPropuestasOfConsulta(consulta.getId());
		StepVerifier.create(props)
			.assertNext(pro1 -> {
				assertEquals(propuesta1.getId(), pro1.getId());
			})
			.assertNext(pro2 -> {
				assertEquals(propuesta2.getId(), pro2.getId());
			})
			.expectComplete()
			.verify()
			;
	}
	
	@Test
	void testKeepUnselectedCosts() {
		addCosts();
		CostesCheckboxWrapper wrapper = new CostesCheckboxWrapper();
		wrapper.setCostes(new ArrayList<>());
		wrapper.getCostes().add(modelMapper.map(cost1, CostesCheckbox.class));
		wrapper.getCostes().add(modelMapper.map(cost2, CostesCheckbox.class));
		wrapper.getCostes().get(0).setSelected(true);
		wrapper.getCostes().get(1).setSelected(false);
		consultaService.keepUnselectedCosts(propuestaProveedor.getId(), wrapper).block();
		
		Mono<Propuesta> prop = consultaService.findPropuestaByPropuestaId(propuestaProveedor.getId());
		StepVerifier.create(prop)
			.assertNext(p ->{
				assertEquals(1, ((PropuestaProveedor)p).getCostes().size());
				assertEquals(cost2.getName(), ((PropuestaProveedor)p).getCostes().get(0).getName());
			})
			.expectComplete()
			.verify()
			;
	}
	
	@Test
	void testGetAllPropuestaProveedorAsignedTo() {
		addCosts();
		Propuesta p2 = new PropuestaProveedor(propuestaProveedor);
		p2.setNombre("propuesta p2");
		p2.setForProposalId("otro");
		Propuesta p3 = new PropuestaProveedor(propuestaProveedor);
		p3.setNombre("propuesta p3");
		consultaService.addPropuesta(consulta.getId(), p2).block();
		consultaService.addPropuesta(consulta.getId(), p3).block();
		
		log.debug("propuestaProveedor asignada a: " + propuestaProveedor.getForProposalId());
		log.debug("p2 asignada a: " + p2.getForProposalId());
		log.debug("p3 asignada a: " + p3.getForProposalId());
		
		Flux<Propuesta> props = consultaService.getAllPropuestaProveedorAsignedTo(propuesta1.getId());
		StepVerifier.create(props)
			.assertNext(p -> {
				log.debug("hay una propuesta asignada: " + p.toString());
				assertEquals(propuestaProveedor.getId(), p.getId());
			})
			.assertNext(p -> {
				log.debug("hay una segunda propuesta asignada: " + p.toString());
				assertEquals(p3.getId(), p.getId());
			})
			.expectComplete()
			.verify()
			;
	}
	
	@Test
	void testKeepUnselectedPvps() {
		addCosts();
		PvpsCheckboxWrapper wrapper = new PvpsCheckboxWrapper();
		wrapper.setPvps(new ArrayList<>());
		wrapper.getPvps().add(modelMapper.map(pvp1, PvperCheckbox.class));
		wrapper.getPvps().add(modelMapper.map(pvp2, PvperCheckbox.class));
		wrapper.getPvps().get(0).setSelected(true);
		wrapper.getPvps().get(1).setSelected(false);
		consultaService.keepUnselectedPvps(propuestaNuestra.getId(), wrapper).block();
		
		Mono<Propuesta> prop = consultaService.findPropuestaByPropuestaId(propuestaNuestra.getId());
		StepVerifier.create(prop)
			.assertNext(p ->{
				assertEquals(1, ((PropuestaNuestra)p).getPvps().size());
				assertEquals(pvp2.getName(), ((PropuestaNuestra)p).getPvps().get(0).getName());
			})
			.expectComplete()
			.verify()
			;
	}

}
