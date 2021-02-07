package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import devs.mrp.gullproject.configuration.ClientProperties;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

/**
 * Exclude WebClient configuration bean in EnableAutoConfiguration
 * and include below ClientConf into SpringBootTest
 * to make the test connect to the mock instead of to Eureka
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK,
				classes = {AtributoServiceProxyWebClient.class, ClientProperties.class})
@AutoConfigureWebTestClient
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@EnableAutoConfiguration
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, ids = { "devs.mrp.gullatributo:GullAtributoService" })
@DirtiesContext
@ActiveProfiles("test")
class AtributoServiceProxyWebClientTest {
	
	@StubRunnerPort("GullAtributoService") int producerPort;
	@Autowired AtributoServiceProxyWebClient service;
	@Autowired ClientProperties clientProperties;

	@Test
	void testValidateDataFormat() throws Exception {
		String type = "STRING";
		String data = "exampleData";
		String wrongType = "INTEGER";
		Mono<Boolean> response;
		
		clientProperties.setAtributoServiceUrl("localhost:" + producerPort + "/");
		
		response = service.validateDataFormat(type, data);
		StepVerifier.create(response)
			.assertNext(cons -> {
				assertEquals(false, cons);
			})
			.expectComplete()
			.verify();
	}

}
