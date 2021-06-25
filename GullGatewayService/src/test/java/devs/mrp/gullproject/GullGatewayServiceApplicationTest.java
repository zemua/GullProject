package devs.mrp.gullproject;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.configuration.RouteConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
, properties = {"atributoService=http://localhost:${wiremock.server.port}",
		"campoService=http://localhost:${wiremock.server.port}"})
@Import({RouteConfiguration.class})
@AutoConfigureWireMock(port = 0)
class GullGatewayServiceApplicationTest {
	
	@LocalServerPort
	private int port;

	private WebTestClient webClient;
	//private MicroServiceConfiguration micro;

	@Autowired
	public GullGatewayServiceApplicationTest(WebTestClient webClient) {
		this.webClient = webClient;
		//this.micro = micro;
	}
	
	@Test
	void testMockRoutes() {
		// A test may only make sense using Spring Contract, still it will not test if the re-reouting is done correctly
		stubFor(get(urlEqualTo("/consultas/test"))
				.willReturn(aResponse()
						.withBody("path to consultas")));
		
		webClient
			.get().uri("/consultas/test")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.isEqualTo("path to consultas");
			});
	}

}
