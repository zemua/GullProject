package devs.mrp.gullproject.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

//@TestConfiguration
public class ClientConf {
	@StubRunnerPort("GullAtributoService") int port;
	
	@Primary
	@Bean
	public WebClient.Builder loadBalancedWebClientBuilder() {
		WebClient.Builder builder = WebClient.builder();
		builder.baseUrl("localhost:".concat(String.valueOf(port)));
		return builder;
	}
}
