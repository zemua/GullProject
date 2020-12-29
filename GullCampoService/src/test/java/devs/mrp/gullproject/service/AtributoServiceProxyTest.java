package devs.mrp.gullproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow.Publisher;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
//import org.springframework.cloud.netflix.ribbon.RibbonClient;
//import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import devs.mrp.gullproject.configuration.FeignResponseDecodeConfig;
import devs.mrp.gullproject.domains.AtributoForCampo;
import reactivefeign.ReactiveOptions;
import reactivefeign.cloud2.CloudReactiveFeign;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactivefeign.webclient.WebReactiveFeign;
import reactivefeign.webclient.WebReactiveOptions;
import reactor.core.publisher.Flux;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

//import com.netflix.loadbalancer.Server;
//import com.netflix.loadbalancer.ServerList;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
/**
 * descartado
 * https://github.com/Playtika/feign-reactive/blob/develop/feign-reactor-test/feign-reactor-spring-configuration-test/src/test/java/reactivefeign/spring/config/ReactiveFeignClientUsingConfigurationsTests.java
 */
@SpringBootTest(classes = {AtributoServiceProxyTest.Application.class, AtributoServiceProxyTest.MockAtributoServiceProxy.class}, webEnvironment = WebEnvironment.NONE)
@DirtiesContext
/**
 * Siguiendo el test de ejemplo en:
 * https://github.com/Playtika/feign-reactive/blob/develop/feign-reactor-test/feign-reactor-spring-mvc-test/src/test/java/reactivefeign/spring/mvc/allfeatures/AllFeaturesMvcTest.java
 */
@EnableAutoConfiguration(exclude = {ReactiveSecurityAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class})
class AtributoServiceProxyTest {
	
	/**
	 * descartados
	 * https://codeburst.io/unit-testing-feignclient-using-restcontroller-and-ribbonclient-4e76bfeddf41
	 * https://github.com/getstarted-spring/feignclient-unit-test
	 * https://medium.com/swlh/spring-boot-how-to-unit-test-a-feign-client-in-isolation-using-only-service-name-4e0fc9e151a7
	 * y load balancer
	 * https://spring.io/guides/gs/spring-cloud-loadbalancer/
	 * https://spring.io/blog/2020/03/25/spring-tips-spring-cloud-loadbalancer
	 * https://spring.io/guides/gs/spring-cloud-loadbalancer/
	 * https://github.com/Playtika/feign-reactive
	 */
	
	// TODO probar MockWebServer / MockRestServiceServer
	// https://blog.mimacom.com/spring-webclient-testing/
	
		
	/**
	 * Configuración de acuerdo al ejemplo de Playtika:
	 * https://github.com/Playtika/feign-reactive/blob/develop/feign-reactor-test/feign-reactor-spring-configuration-test/src/test/java/reactivefeign/spring/config/ReactiveFeignClientUsingConfigurationsTests.java
	 * utilizando wiremock
	 * https://github.com/tomakehurst/wiremock
	 * 
	 * Siguiendo el test de ejemplo en:
	 * https://github.com/Playtika/feign-reactive/blob/develop/feign-reactor-test/feign-reactor-spring-mvc-test/src/test/java/reactivefeign/spring/mvc/allfeatures/AllFeaturesMvcTest.java
	 * con algunas modificaciones
	 */
	
	
	/**
	 * con wiremock siguiendo el ejemplo de:
	 * https://rieckpil.de/spring-boot-integration-tests-with-wiremock-and-junit-5/
	 * https://github.com/rieckpil/blog-tutorials/tree/master/spring-boot-integration-tests-wiremock
	 * y algo de
	 * https://github.com/Playtika/feign-reactive/blob/develop/feign-reactor-test/feign-reactor-spring-configuration-test/src/test/java/reactivefeign/spring/config/ReactiveFeignClientUsingConfigurationsTests.java
	 */
	
	static final String MOCK_SERVER_PORT_PROPERTY = "mock.server.port";
	private static WireMockServer mockHttpServer = new WireMockServer(wireMockConfig().dynamicPort());
	
	@ReactiveFeignClient(name = "foo", url = "http://localhost:${" + MOCK_SERVER_PORT_PROPERTY+"}")
	public static interface MockAtributoServiceProxy extends AtributoServiceProxy{
	}
	
	@Autowired
	MockAtributoServiceProxy mockAtributoServiceProxy;
	
	@BeforeAll
	public static void setupStubs() {

		mockHttpServer.stubFor(get(urlPathEqualTo("/api/atributos/data-validator")) // https://wiremock.org/docs/request-matching/
				.withQueryParam("type", equalTo("tipo")) // https://www.petrikainulainen.net/programming/testing/wiremock-tutorial-request-matching-part-two/
				.withQueryParam("data", equalTo("datos"))
				.willReturn(aResponse()
						.withBody("true")
						.withHeader("Content-Type", "application/json")));
		
		mockHttpServer.stubFor(get(urlPathEqualTo("/api/atributos/data-validator")) // https://wiremock.org/docs/request-matching/
				.withQueryParam("type", equalTo("negativo")) // https://www.petrikainulainen.net/programming/testing/wiremock-tutorial-request-matching-part-two/
				.withQueryParam("data", equalTo("pbida"))
				.willReturn(aResponse()
						.withBody("false")
						.withHeader("Content-Type", "application/json")));

		
		ObjectMapper mapper = new ObjectMapper();
		AtributoForCampo atr = new AtributoForCampo();
		atr.setId("urlid");
		atr.setName("nombre");
		atr.setTipo("tipo");
		JsonNode node = mapper.convertValue(atr, JsonNode.class);
		
		mockHttpServer.stubFor(get(urlEqualTo("/api/atributos/idforcampo/urlid"))
				.willReturn(aResponse()
						.withJsonBody(node)
						.withHeader("Content-Type", "application/json")));

		mockHttpServer.start();

		System.setProperty(MOCK_SERVER_PORT_PROPERTY, Integer.toString(mockHttpServer.port()));
	}
	
	@AfterAll
	public static void teardown() {
		mockHttpServer.stop();
	}
	
	@Test // https://github.com/Playtika/feign-reactive/blob/develop/feign-reactor-test/feign-reactor-spring-configuration-test/src/test/java/reactivefeign/spring/config/ReactiveFeignClientUsingConfigurationsTests.java
	public void testValidateDataFormat() {
		Boolean response = mockAtributoServiceProxy.validateDataFormat("tipo", "datos").block();
		assertEquals(true, response);
		
		Boolean response2 = mockAtributoServiceProxy.validateDataFormat("negativo", "pbida").block();
		assertEquals(false, response2);
	}
	
	@Test // https://github.com/Playtika/feign-reactive/blob/develop/feign-reactor-test/feign-reactor-spring-configuration-test/src/test/java/reactivefeign/spring/config/ReactiveFeignClientUsingConfigurationsTests.java
	public void testGetAtributoForCampoById() {
		AtributoForCampo response = mockAtributoServiceProxy.getAtributoForCampoById("urlid").block();
		
		AtributoForCampo atr = new AtributoForCampo();
		atr.setId("urlid");
		atr.setName("nombre");
		atr.setTipo("tipo");
		
		assertEquals(atr, response);
	}
	
	@Configuration
	@EnableAutoConfiguration
	@EnableReactiveFeignClients(defaultConfiguration = DefaultConfiguration.class,
			clients = {AtributoServiceProxyTest.MockAtributoServiceProxy.class})
	protected static class Application {
	}
	
	@Configuration
	protected static class DefaultConfiguration {
		@Bean
		public ReactiveOptions reactiveFeignOptions(){
			return new WebReactiveOptions.Builder()
					.setReadTimeoutMillis(5000)
					.setConnectTimeoutMillis(5000)
					.build();
		}
	}

}
