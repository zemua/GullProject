package devs.mrp.gullproject.rest;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.models.LineaRepresentationModel;
import devs.mrp.gullproject.domains.models.LineaRepresentationModelAssembler;
import devs.mrp.gullproject.repository.CampoRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LineaRestControllerTest {
	
	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	@Autowired
	LineaRestController lineaRestController;
	
	@MockBean
	CampoRepo campoRepo; // evita problemas en el contexto del test con configuracion auto @SpringBootTest en lugar de @WebFluxTest
	@MockBean
	LineaRepo lineaRepo;
	@MockBean
	LineaRepresentationModelAssembler lrma;

	@Test
	void testGetAllLineas() {
		
		// WebTestClient client = webTestClient.mutateWith(configurer);
		WebTestClient client = WebTestClient.bindToController(lineaRestController).build().mutateWith(configurer);

		Campo<Integer> m = new Campo<>();
		m.setAtributoId("id_del_campo");
		m.setDatos(235);
		m.setId("id_aleatoria");
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(m);
		
		Linea l = new Linea();
		l.setId("linea_id");
		l.setNombre("linea_name");
		l.setCampos(campos);
		Flux<Linea> flux = Flux.just(l);
		
		when(lineaRepo.findAll()).thenReturn(flux);
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
		
		// el último when toma precedencia
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

}
