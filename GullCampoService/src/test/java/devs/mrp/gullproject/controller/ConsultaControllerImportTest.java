package devs.mrp.gullproject.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.TipoPropuesta;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaService;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ConsultaControllerImportTest {
	
	@Autowired WebTestClient webTestClient;
	@Autowired ConsultaController consultaController;
	@Autowired LineaService lineaService;
	@Autowired ConsultaService consultaService;
	@Autowired LineaRepo lineaRepo;
	@Autowired ConsultaRepo consultaRepo;
	
	private PropuestaCliente pc;
	private PropuestaProveedor pp;
	private Consulta consulta;
	
	@BeforeEach
	void setup() {
		lineaRepo.deleteAll().block();
		consultaRepo.deleteAll().block();
		
		consulta = new Consulta();
		consulta.setNombre("nombre conzurta");
		pc = new PropuestaCliente();
		pc.setNombre("nombre consulta cliente pc");
		pp = new PropuestaProveedor();
		pp.setForProposalId(pc.getId());
		pp.setLineasAsignadas(2);
		pp.setNombre("nombre propuesta pp");
		consulta.setPropuestas(List.of(pc, pp));
		
		consultaService.save(consulta).block();
		
		Linea l1 = new Linea();
		Linea l2 = new Linea();
		l1.setPropuestaId(pp.getId());
		l2.setPropuestaId(pp.getId());
		
		lineaService.addLinea(l1).block();
		lineaService.addLinea(l2).block();
		
		StepVerifier.create(lineaService.findAll())
		.assertNext(l -> {})
		.assertNext(l -> {})
		.expectComplete()
		.verify();
		
		StepVerifier.create(consultaService.findById(consulta.getId()))
		.assertNext(c -> {
			assertEquals(2, c.getPropuestas().size());
		})
		.expectComplete()
		.verify();
	}
	
	@AfterEach
	void cleanup() {
		lineaRepo.deleteAll().block();
		consultaRepo.deleteAll().block();
	}
	
	@Test
	@WithMockUser
	void testImportProposalProveedorToProposalCliente() {
		webTestClient.get()
		.uri("/consultas/revisar/id/" + consulta.getId() + "/onprop/" + pc.getId() + "/importcotizacion")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Clonar Cotización")
					.contains(pp.getNombre())
					.contains(pc.getNombre())
					;
		});
	}
	
	@Test
	@WithMockUser
	void testProcessImportProposalProveedorToProposalCliente() {
		webTestClient.post()
		.uri("/consultas/revisar/id/" + consulta.getId() + "/onprop/" + pc.getId() + "/importcotizacion")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("string", pp.getId()))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains(pp.getNombre())
					.contains("Cotización clonada:");
		});
		
		StepVerifier.create(lineaService.findAll())
		.assertNext(l -> {})
		.assertNext(l -> {})
		.assertNext(l -> {})
		.assertNext(l -> {})
		.expectComplete()
		.verify();
		
		StepVerifier.create(consultaService.findById(consulta.getId()))
		.assertNext(c -> {
			assertEquals(3, c.getPropuestas().size());
		})
		.expectComplete()
		.verify();
	}

}
