package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import devs.mrp.gullproject.configuration.ClientProperties;
import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.StringWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
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
	
	public Mono<AtributoForCampo> getAtributoForCampoById(String id) {
		// /api/atributos/idforcampo/{id}
		return webClientBuilder.build().get()
				.uri(clientProperties.getAtributoServiceUrl().concat("api/atributos/idforcampo/").concat(id))
				.header("Content-Type", "text/html")
				.retrieve().bodyToMono(AtributoForCampo.class);
	}
	
	public Flux<AtributoForCampo> getAllAtributos() {
		return webClientBuilder.build().get()
				.uri(clientProperties.getAtributoServiceUrl().concat("api/atributos/all"))
				.header("Content-Type", "text/html")
				.retrieve().bodyToFlux(AtributoForCampo.class);
	}
	
	public Flux<StringWrapper> getAllDataFormats() {
		return webClientBuilder.build().get()
				.uri(clientProperties.getAtributoServiceUrl().concat("api/atributos/formatos"))
				.header("Content-Type", "text/html")
				.retrieve().bodyToFlux(StringWrapper.class);
	}
	
	// TODO call to get the data type that needs to be used for a DataFormat
	
}
