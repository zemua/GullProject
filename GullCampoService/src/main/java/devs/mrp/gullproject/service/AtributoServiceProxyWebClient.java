package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import devs.mrp.gullproject.configuration.ClientProperties;
import reactor.core.publisher.Mono;

public class AtributoServiceProxyWebClient {
	
	// TODO complete & test with contract
	
	WebClient.Builder webClientBuilder;
	ClientProperties clientProperties;
	
	@Autowired
	public AtributoServiceProxyWebClient(WebClient.Builder webClientBuilder, ClientProperties clientProperties) {
		this.webClientBuilder = webClientBuilder;
		this.clientProperties = clientProperties;
	}
	
	public Mono<Boolean> validateDataFormat(String type, String data){
		return webClientBuilder.build().get().uri(uriBuilder -> uriBuilder
				//.path("/atributo-service/api/atributos/data-validator")
				.path(clientProperties.getAtributoServiceUrl().concat("api/atributos/data-validator"))
				.queryParam("type", type)
				.queryParam("data", data)
				.build())
				.header("Content-Type", "text/html")
				.retrieve().bodyToMono(Boolean.class);
	}
	
}
