package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class AtributoServiceProxyWebClient {
	
	// TODO test y contract
	
	WebClient.Builder webClientBuilder;
	
	@Autowired
	public AtributoServiceProxyWebClient(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}
	
	public Mono<Boolean> validateDataFormat(String type, String data){
		return webClientBuilder.build().get().uri(uriBuilder -> uriBuilder
				.path("/atributo-service/api/atributos/data-validator")
				.queryParam("type", type)
				.queryParam("data", data)
				.build())
				.retrieve().bodyToMono(Boolean.class);
	}
	
}
