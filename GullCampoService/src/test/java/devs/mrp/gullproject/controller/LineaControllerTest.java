package devs.mrp.gullproject.controller;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = LineaController.class)
@AutoConfigureWebTestClient
class LineaControllerTest {
	
	WebTestClient webTestClient;
	LineaController lineaController;
	
	@MockBean
	LineaService lineaService;
	@MockBean
	ConsultaService consultaService;
	
	@Autowired
	public LineaControllerTest(WebTestClient webTestClient, LineaController lineaController) {
		this.webTestClient = webTestClient;
		this.lineaController = lineaController;
	}
	
	Linea linea1;
	Campo<String> campo1a;
	Campo<Integer> campo1b;
	
	Linea linea2;
	Campo<String> campo2a;
	Campo<Integer> campo2b;
	
	Mono<Linea> mono1;
	Mono<Linea> mono2;
	Flux<Linea> flux;
	
	Propuesta propuesta;
	
	@BeforeEach
	void init() {
		campo1a = new Campo<>();
		campo1a.setAtributoId("atributo1");
		campo1a.setDatos("datos1");
		campo1b = new Campo<>();
		campo1b.setAtributoId("atributo2");
		campo1b.setDatos(123);
		linea1 = new Linea();
		linea1.addCampo(campo1a);
		linea1.addCampo(campo1b);
		
		campo2a = new Campo<>();
		campo2a.setAtributoId("atributo1");
		campo2a.setDatos("datos2");
		campo2b = new Campo<>();
		campo2b.setAtributoId("atributo2");
		campo2b.setDatos(321);
		linea2 = new Linea();
		linea2.addCampo(campo2a);
		linea2.addCampo(campo2b);
		
		mono1 = Mono.just(linea1);
		mono2 = Mono.just(linea2);
		flux = Flux.just(linea1, linea2);
		
		propuesta = new PropuestaCliente();
		propuesta.setNombre("propuestaName");
	}

	@Test
	void testShowAllLinesOf() {
		when(lineaService.findByPropuestaId(propuesta.getId())).thenReturn(flux);
		when(consultaService.findPropuestaByPropuestaId(propuesta.getId())).thenReturn(Mono.just(propuesta));
		
		webTestClient.get()
			.uri("/lineas/allof/propid/" + propuesta.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Lineas de la propuesta")
						.contains("Crear nueva linea")
						.contains("Nombre")
						.contains("Campos")
						.contains("Enlace")
						.contains(propuesta.getNombre());
			});
	}

}
