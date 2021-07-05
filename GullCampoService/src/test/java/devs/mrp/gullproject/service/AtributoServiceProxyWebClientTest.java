package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import devs.mrp.gullproject.configuration.ClientProperties;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domainsdto.StringWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exclude WebClient configuration bean in EnableAutoConfiguration
 * and include below ClientConf into SpringBootTest
 * to make the test connect to the mock instead of to Eureka
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK,
				classes = {AtributoServiceProxyWebClient.class, ClientProperties.class, ProxyUtils.class})
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@EnableAutoConfiguration
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, ids = { "devs.mrp.gullatributo:GullAtributoService" })
@DirtiesContext
@ActiveProfiles("test")
class AtributoServiceProxyWebClientTest {
	
	@StubRunnerPort("GullAtributoService") static int producerPort;
	@Autowired AtributoServiceProxyWebClient service;
	@Autowired ClientProperties clientProperties;
	
	@BeforeEach
	void setup() {
		clientProperties.setAtributoServiceUrl("localhost:" + producerPort + "/");
		clientProperties.setAtributoServiceHost("localhost");
	}

	@Test
	void testValidateDataFormat() throws Exception {
		String type = "DESCRIPCION";
		String data = "correct dataaaaaa";
		Mono<Boolean> response;
		
		response = service.validateDataFormat(type, data);
		StepVerifier.create(response)
			.assertNext(cons -> {
				assertEquals(true, cons);
			})
			.expectComplete()
			.verify();
		
		type = "CANTIDAD";
		data = "8569742";
		
		response = service.validateDataFormat(type, data);
		StepVerifier.create(response)
			.assertNext(cons -> {
				assertEquals(true, cons);
			})
			.expectComplete()
			.verify();
		
		type = "CANTIDAD";
		data = "asdqwe";
		
		response = service.validateDataFormat(type, data);
		StepVerifier.create(response)
			.assertNext(cons -> {
				assertEquals(false, cons);
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	void testGetAtributoForCampoById() {
		String id = "unID123";
		
		Mono<AtributoForCampo> response;
		
		response = service.getAtributoForCampoById(id);
		StepVerifier.create(response)
			.assertNext(afc -> {
				assertEquals("unID123", afc.getId());
				assertEquals("response name", afc.getName());
				assertEquals("RESPONSETYPE", afc.getTipo());
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	void testGetAllAtributos() {
		Flux<AtributoForCampo> response;
		
		response = service.getAllAtributos();
		StepVerifier.create(response)
			.assertNext(at -> {
				assertEquals("unID111", at.getId());
				assertEquals("response name one", at.getName());
				assertEquals("RESPONSETYPEONE", at.getTipo());
			})
			.assertNext(at -> {
				assertEquals("unID222", at.getId());
				assertEquals("response name two", at.getName());
				assertEquals("RESPONSETYPETWO", at.getTipo());
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	void testGetAllDataFormats() {
		Flux<StringWrapper> response;
		
		response = service.getAllDataFormats();
		StepVerifier.create(response)
			.assertNext(st -> assertEquals("DESCRIPCION", st.getString()))
			.assertNext(st -> assertEquals("CANTIDAD", st.getString()))
			.assertNext(st -> assertEquals("COSTE", st.getString()))
			.assertNext(st -> assertEquals("MARGEN", st.getString()))
			.assertNext(st -> assertEquals("PVP", st.getString()))
			.assertNext(st -> assertEquals("PLAZO", st.getString()))
			.expectComplete()
			.verify();
	}
	
	@Test
	void testGetClassTypeOfFormat() {
		Mono <String> response;
		
		response = service.getClassTypeOfFormat("DESCRIPCION");
		StepVerifier.create(response)
			.assertNext(r -> assertEquals("String", r))
			.expectComplete()
			.verify();
		
		response = service.getClassTypeOfFormat("CANTIDAD");
		StepVerifier.create(response)
			.assertNext(r -> assertEquals("Integer", r))
			.expectComplete()
			.verify();
	}
	
	@Test
	void testGetAtributosByArrayOfIds() {
		Flux<AtributoForCampo> response;
		
		List<String> arrayList = new ArrayList<>();
		arrayList.add("pr1m3r0");
		arrayList.add("s3g7nd0");
		arrayList.add("t3rc3r0");
		
		response = service.getAtributosByArrayOfIds(arrayList);
		StepVerifier.create(response)
		.assertNext(st -> {
			assertEquals("s3g7nd0", st.getId());
			assertEquals("response name two", st.getName());
			assertEquals("RESPONSETYPETWO", st.getTipo());
		})
		.assertNext(st -> {
			assertEquals("t3rc3r0", st.getId());
			assertEquals("response name three", st.getName());
			assertEquals("RESPONSETYPETHREE", st.getTipo());
		})
		.expectComplete()
		.verify();
	}

}
