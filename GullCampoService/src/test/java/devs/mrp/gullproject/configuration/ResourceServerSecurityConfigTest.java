package devs.mrp.gullproject.configuration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.controller.ConsultaController;
import devs.mrp.gullproject.domains.ConsultaFactory;
import devs.mrp.gullproject.domains.ConsultaImpl;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckboxedCostToPvperImpl;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.service.AtributoUtilities;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.propuesta.PropuestaUtilities;
import devs.mrp.gullproject.service.propuesta.proveedor.CotizacionOfCostMapperFactoryImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class ResourceServerSecurityConfigTest {

	@Autowired
	private WebTestClient client;
	
	@Test
	@WithMockUser
	void testCallShowallWithValidUser() {
		client.get()
			.uri("/consultas/all")
			.exchange()
			.expectStatus().isOk()
			;
	}
	
	@Test
	void testUnauthorized() { 
		client.get()
			.uri("/consultas/all")
			.exchange()
			.expectStatus().is4xxClientError()
			;
	}

}
