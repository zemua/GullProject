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
import devs.mrp.gullproject.domains.PropuestaProveedor;
import devs.mrp.gullproject.domains.dto.CostesCheckbox;
import devs.mrp.gullproject.domains.dto.CostesCheckboxWrapper;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.CustomConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
	Propuesta propuestaProveedor;
	Mono<Consulta> mono;
	AtributoForCampo att1;
	AtributoForCampo att2;
	CosteProveedor cost1;
	CosteProveedor cost2;
	
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
		propuestaProveedor.operations().addAttribute(att1);
		propuestaProveedor.setNombre("nombre propuesta proveedor");
		propuestaProveedor.operations().addAttribute(att2);
		cost1 = new CosteProveedor();
		cost1.setName("coste 1");
		cost2 = new CosteProveedor();
		cost2.setName("coste 2");
		List<CosteProveedor> costs = new ArrayList<>();
		costs.add(cost1);
		costs.add(cost2);
		((PropuestaProveedor)propuestaProveedor).setCostes(costs);
		consultaService.addPropuesta(consulta.getId(), propuestaProveedor).block();
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

}
