package devs.mrp.gullproject.rest;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.domains.models.ConsultaRepresentationModel;
import devs.mrp.gullproject.domains.models.ConsultaRepresentationModelAssembler;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.ConsultaService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ConsultaRestController.class)
@Import({ConsultaService.class, ConsultaByIdRestController.class, ModelMapper.class, LineaFactory.class, Consulta.class})
class ConsultaRestControllerTest {
	
	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	@Autowired
	ConsultaRestController consultaRestController;
	@Autowired
	ConsultaByIdRestController consultaByIdRestController;
	
	@MockBean
	ConsultaRepo consultaRepo;
	@MockBean
	LineaRepo lineaRepo;
	@MockBean
	ConsultaRepresentationModelAssembler crma;
	
	Consulta consulta1;
	Consulta consulta2;
	Flux<Consulta> flux;
	Mono<Consulta> mono;
	ConsultaRepresentationModel model1;
	ConsultaRepresentationModel model2;
	
	@BeforeEach
	void initialize() {
		consulta1 = new Consulta();
		consulta1.setCreatedTime(123456L);
		consulta1.setId("id1");
		consulta1.setNombre("nombre1");
		consulta1.setPropuestas(new ArrayList<Propuesta>());
		consulta1.setStatus("status1");
		
		consulta2 = new Consulta();
		consulta2.setCreatedTime(654321L);
		consulta2.setId("id2");
		consulta2.setNombre("nombre2");
		consulta2.setPropuestas(new ArrayList<Propuesta>());
		consulta2.setStatus("status2");
		
		flux = Flux.just(consulta1, consulta2);
		mono = Mono.just(consulta1);
		
		model1 = new ConsultaRepresentationModel();
		model1.setCreatedTime(consulta1.getCreatedTime());
		model1.setEditedTime(consulta1.getEditedTime());
		model1.setId(consulta1.getId());
		model1.setNombre(consulta1.getNombre());
		model1.setPropuestas(consulta1.getPropuestas());
		model1.setStatus(consulta1.getStatus());
		model1.add(Link.of("link1"));
		
		model2 = new ConsultaRepresentationModel();
		model2.setCreatedTime(consulta2.getCreatedTime());
		model2.setEditedTime(consulta2.getEditedTime());
		model2.setId(consulta2.getId());
		model2.setNombre(consulta2.getNombre());
		model2.setPropuestas(consulta2.getPropuestas());
		model2.setStatus(consulta2.getStatus());
		model2.add(Link.of("link2"));
	}

	@Test
	void testGetAllConsultas() {
		WebTestClient client = WebTestClient.bindToController(consultaRestController).build().mutateWith(configurer);
		
		when(consultaRepo.findAllByOrderByCreatedTimeDesc()).thenReturn(flux);
		
		when(crma.toModel(ArgumentMatchers.any(Consulta.class))).thenReturn(null);
		when(crma.toModel(ArgumentMatchers.eq(consulta1))).thenReturn(model1);
		when(crma.toModel(ArgumentMatchers.eq(consulta2))).thenReturn(model2);
		
		client.get()
			.uri("/api/consultas/all").exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("id1")
				.contains("id2")
				.contains("nombre1")
				.contains("nombre2")
				.contains("link1")
				.contains("link2");
			});
	}
	
	@Test
	void testGetConsultaById() {
		//WebTestClient client = webTestClient.mutateWith(configurer);
		WebTestClient client = WebTestClient.bindToController(consultaByIdRestController).build().mutateWith(configurer);

		when(consultaRepo.findById(ArgumentMatchers.eq("idconsulta"))).thenReturn(mono);
		when(crma.toModel(ArgumentMatchers.eq(consulta1))).thenReturn(model1);

		client.get()
		.uri("/api/consultas/id/idconsulta")
		.exchange()
		.expectStatus().isOk()
		.expectBody(String.class)
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("id1")
			.doesNotContain("id2")
			.contains("nombre1")
			.doesNotContain("nombre2")
			.contains("link1")
			.doesNotContain("link2");
		});
	}

}
