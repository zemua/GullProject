package devs.mrp.gullproject.rest;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import devs.mrp.gullproject.repository.LineaRepo;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LineaByIdRestControllerTest {

	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	@Autowired
	LineaByIdRestController lineaByIdRestController;
	
	@MockBean
	LineaRepo lineaRepo;
	@MockBean
	LineaRepresentationModelAssembler lrma;
	
	
	@Test
	void testGetLineaById() {
		
		// WebTestClient client = webTestClient.mutateWith(configurer);
		WebTestClient client = WebTestClient.bindToController(lineaByIdRestController).build().mutateWith(configurer);

		Campo<Integer> m = new Campo<>();
		m.setAtributoId("id_del_campo");
		m.setDatos(235);
		m.setId("id_aleatoria");
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(m);
		
		Linea l = new Linea();
		l.setNombre("nombre_linea");
		l.setId("id_linea");
		l.setCampos(campos);
		Mono<Linea> mono = Mono.just(l);
		
		when(lineaRepo.findById(ArgumentMatchers.eq("id_linea"))).thenReturn(mono);
		
		LineaRepresentationModel lrm = new LineaRepresentationModel();
		lrm.setCampos(l.getCampos());
		lrm.setId(l.getId());
		lrm.setNombre(l.getNombre());
		lrm.add(Link.of("/api/esto/es/un/link"));
		when(lrma.toModel(ArgumentMatchers.eq(l))).thenReturn(lrm);
		
		client.get()
			.uri("/api/lineas/id/id_linea")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(new BaseMatcher<String>() {

				@Override
				public boolean matches(Object actual) {
					return actual.toString().contains("id_del_campo")
							&& actual.toString().contains("id_aleatoria")
							&& actual.toString().contains("235")
							&& actual.toString().contains("nombre_linea")
							&& actual.toString().contains("id_linea");
				}

				@Override
				public void describeTo(Description description) {
				}
				
			});
	}

}
