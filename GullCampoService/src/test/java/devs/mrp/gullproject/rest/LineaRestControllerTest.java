package devs.mrp.gullproject.rest;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.domains.models.LineaRepresentationModel;
import devs.mrp.gullproject.domains.models.LineaRepresentationModelAssembler;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@Import({LineaFactory.class, Consulta.class})
class LineaRestControllerTest {
	
	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	@Autowired
	LineaRestController lineaRestController;
	@Autowired
	LineaFactory lineaFactory;
	
	@MockBean
	LineaRepo lineaRepo;
	@MockBean
	LineaService lineaService;
	@MockBean
	LineaRepresentationModelAssembler lrma;

	@Test
	void testGetAllLineas() {
		
		WebTestClient client = WebTestClient.bindToController(lineaRestController).build().mutateWith(configurer);

		Campo<Integer> m = new Campo<>();
		m.setAtributoId("id_del_campo");
		m.setDatos(235);
		m.setId("id_aleatoria");
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(m);
		
		Linea l = lineaFactory.create();
		l.setId("linea_id");
		l.setNombre("linea_name");
		l.setCampos(campos);
		Flux<Linea> flux = Flux.just(l);
		
		when(lineaService.findAll()).thenReturn(flux);
		LineaRepresentationModel lrm = new LineaRepresentationModel();
		lrm.setCampos(l.getCampos());
		lrm.setId(l.getId());
		lrm.setNombre(l.getNombre());
		lrm.add(Link.of("/api/esto/es/un/link"));
		
		LineaRepresentationModel lrm2 = new LineaRepresentationModel();
		lrm2.setCampos(null);
		lrm2.setId("sin_id");
		lrm2.setNombre("sin_nombre");
		lrm2.add(Link.of("sin_link"));
		
		// el ??ltimo when toma precedencia
		when(lrma.toModel(ArgumentMatchers.any())).thenReturn(lrm2);
		when(lrma.toModel(ArgumentMatchers.eq(l))).thenReturn(lrm);
		
		client.get()
			.uri("/api/lineas/all").exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(new BaseMatcher<String>() {

				@Override
				public boolean matches(Object actual) {
					return actual.toString().contains("linea_name")
							&& actual.toString().contains("linea_id")
							&& actual.toString().contains("id_aleatoria")
							&& actual.toString().contains("id_del_campo");
				}

				@Override
				public void describeTo(Description description) {					
				}
				
			});
	}
	
	@Test
	void testCrearLinea() {
		
		WebTestClient client = WebTestClient.bindToController(lineaRestController).build().mutateWith(configurer);

		Campo<Integer> m = new Campo<>();
		m.setAtributoId("id_del_campo");
		m.setDatos(235);
		m.setId("id_aleatoria");
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(m);
		
		Linea l = lineaFactory.create();
		l.setId("linea_id");
		l.setNombre("linea_name");
		l.setCampos(campos);
		Mono<Linea> mono = Mono.just(l);
		
		when(lineaService.addLinea(ArgumentMatchers.refEq(l))).thenReturn(mono);
		LineaRepresentationModel lrm = new LineaRepresentationModel();
		lrm.setCampos(l.getCampos());
		lrm.setId(l.getId());
		lrm.setNombre(l.getNombre());
		lrm.add(Link.of("/api/esto/es/un/link"));
		
		LineaRepresentationModel lrm2 = new LineaRepresentationModel();
		lrm2.setCampos(null);
		lrm2.setId("sin_id");
		lrm2.setNombre("sin_nombre");
		lrm2.add(Link.of("sin_link"));
		
		// el ??ltimo when toma precedencia
		when(lrma.toModel(ArgumentMatchers.any())).thenReturn(lrm2);
		when(lrma.toModel(ArgumentMatchers.eq(l))).thenReturn(lrm);
		
		client.post()
			.uri("/api/lineas/nueva")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(l),  Linea.class)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("linea_id")
				.contains("linea_name")
				.contains("id_del_campo")
				.contains("/api/esto/es/un/link")
				.doesNotContain("sin_id")
				.doesNotContain("sin_nombre")
				.doesNotContain("sin_link");
			});
	}
	
	@Test
	void testActualizarLinea() {
		WebTestClient client = WebTestClient.bindToController(lineaRestController).build().mutateWith(configurer);

		Campo<Integer> m = new Campo<>();
		m.setAtributoId("id_del_campo");
		m.setDatos(235);
		m.setId("id_aleatoria");
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(m);
		
		Linea l = lineaFactory.create();
		l.setId("linea_id");
		l.setNombre("linea_name");
		l.setCampos(campos);
		Mono<Linea> mono = Mono.just(l);
		
		when(lineaService.updateLinea(ArgumentMatchers.refEq(l))).thenReturn(mono);
		LineaRepresentationModel lrm = new LineaRepresentationModel();
		lrm.setCampos(l.getCampos());
		lrm.setId(l.getId());
		lrm.setNombre(l.getNombre());
		lrm.add(Link.of("/api/esto/es/un/link"));
		
		LineaRepresentationModel lrm2 = new LineaRepresentationModel();
		lrm2.setCampos(null);
		lrm2.setId("sin_id");
		lrm2.setNombre("sin_nombre");
		lrm2.add(Link.of("sin_link"));
		
		// el ??ltimo when toma precedencia
		when(lrma.toModel(ArgumentMatchers.any())).thenReturn(lrm2);
		when(lrma.toModel(ArgumentMatchers.eq(l))).thenReturn(lrm);
		
		client.put()
			.uri("/api/lineas/actualizar-una")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(l),  Linea.class)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("linea_id")
				.contains("linea_name")
				.contains("id_del_campo")
				.contains("/api/esto/es/un/link")
				.doesNotContain("sin_id")
				.doesNotContain("sin_nombre")
				.doesNotContain("sin_link");
			});
	}
	
	@Test
	void testBorrarLineaById() {
		WebTestClient client = WebTestClient.bindToController(lineaRestController).build().mutateWith(configurer);

		Campo<Integer> m = new Campo<>();
		m.setAtributoId("id_del_campo");
		m.setDatos(235);
		m.setId("id_aleatoria");
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(m);
		
		Linea l = lineaFactory.create();
		l.setId("linea_id");
		l.setNombre("linea_name");
		l.setCampos(campos);
		
		when(lineaService.deleteLineaById(ArgumentMatchers.eq(l.getId()))).thenReturn(Mono.empty());
		
		client.delete()
			.uri("/api/lineas/borrar-una?id=linea_id")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			/*.consumeWith(response -> { // result is null, cannot be evaluated, just response ok is enough
				Assertions.assertThat(response.getResponseBody()).asString()
				.isEqualTo(null);
			})*/;
	}

}
