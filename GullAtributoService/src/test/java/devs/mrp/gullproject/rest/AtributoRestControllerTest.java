package devs.mrp.gullproject.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.domains.StringWrapper;
import devs.mrp.gullproject.domains.DTO.AtributoDTO;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModel;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModelAssembler;
import devs.mrp.gullproject.domains.representationmodels.AtributoRespresentationModelMapperImpl;
import devs.mrp.gullproject.repositorios.AtributoRepo;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
//@WebFluxTest(controllers = AtributoRestController.class)
//@Import({AtributoService.class, ModelMapper.class, AtributoRespresentationModelMapperImpl.class})
class AtributoRestControllerTest {
	
	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	@Autowired
	AtributoRestController atributoRestController;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	WebTestClient client;
	
	/*@MockBean
	AtributoRepresentationModelAssembler arma;*/
	@MockBean
	AtributoRepo atributoRepo;

	@Test
	@WithMockUser
	void testgetAllAtributos() {
		
		// Create a WebTestClient by binding to the controller and applying the hypermedia configurer.
		//WebTestClient client = WebTestClient.bindToController(atributoRestController).build().mutateWith(configurer);
		
		Atributo m = new Atributo();
		m.setName("bonnet");
		m.setId("idaleatoria");
		m.setTipo(DataFormat.DESCRIPCION);
		m.setValoresFijos(true);
		Flux<Atributo> mFlux = Flux.just(m);
		
		when(atributoRepo.findAll()).thenReturn(mFlux);
		when(atributoRepo.findAllByOrderByOrdenAsc()).thenReturn(mFlux);
		/*AtributoRepresentationModel mrm = new AtributoRepresentationModel();
		mrm.setId(m.getId());
		mrm.setName(m.getName());
		mrm.setTipo(m.getTipo());
		mrm.setValoresFijos(m.isValoresFijos());
		mrm.add(Link.of("/api/esto/es/un/link"));
		when(arma.toModel(ArgumentMatchers.eq(m))).thenReturn(mrm);*/
		
		client.get()
			.uri("/api/atributos/all")
			//.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("bonnet")
					.contains("idaleatoria")
					.contains("DESCRIPCION")
					.contains("/api/atributos/id/idaleatoria");
			});
		
	}
	
	@Test
	@WithMockUser
	public void testModelMapper() {
		Atributo a = new Atributo();
		a.setId("id");
		a.setName("nombre");
		a.setTipo(DataFormat.NUMERO);
		
		AtributoDTO dto = modelMapper.map(a, AtributoDTO.class);
		assertEquals(a.getId(), dto.getId());
		assertEquals(a.getName(), dto.getName());
		assertEquals(a.getTipo(), dto.getTipo());
	}
	
	@Test
	@WithMockUser
	public void testGetAtributoForCampoById() {
		
		// por aprender a usar... aunque con reactor da problemas...
		// https://www.baeldung.com/spring-cloud-contract
		
		//WebTestClient client = WebTestClient.bindToController(atributoRestController).build();
		
		Atributo m = new Atributo();
		m.setName("bonnet");
		m.setId("idaleatoria");
		m.setTipo(DataFormat.DESCRIPCION);
		m.setValoresFijos(true);
		Mono<Atributo> mono = Mono.just(m);
		
		when(atributoRepo.findById(ArgumentMatchers.eq(m.getId()))).thenReturn(mono);
		
		client.get()
		.uri("/api/atributos/idforcampo/idaleatoria")
		//.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody(String.class)
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
				.contains("bonnet")
				.contains("idaleatoria")
				.contains("DESCRIPCION")
				.doesNotContain("esto/es/un/link")
				.doesNotContain("valoresFijos");
		});
	}
	
	@Test
	@WithMockUser
	public void testGetTodosLosDataFormat() {
		Flux<StringWrapper> flux = atributoRestController.getTodosLosDataFormat();
		StepVerifier
			.create(flux)
			.expectNext(new StringWrapper("DESCRIPCION"))
			.expectNext(new StringWrapper("NUMERO"))
			.expectNext(new StringWrapper("DECIMAL"))
			.verifyComplete();
	}

}
