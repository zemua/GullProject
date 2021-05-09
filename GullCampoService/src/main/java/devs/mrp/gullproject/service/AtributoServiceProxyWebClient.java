package devs.mrp.gullproject.service;

import java.util.List;
import java.util.ListIterator;

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
	
	WebClient.Builder webClientBuilder;
	ClientProperties clientProperties;
	
	@Autowired
	public AtributoServiceProxyWebClient(WebClient.Builder webClientBuilder, ClientProperties clientProperties) {
		this.webClientBuilder = webClientBuilder;
		this.clientProperties = clientProperties;
	}
	
	public Mono<Boolean> validateDataFormat(String type, String data){
		return webClientBuilder.build().get().uri(uriBuilder -> uriBuilder
				.path(clientProperties.getAtributoServiceUrl().concat("api/atributos/data-validator"))
				.queryParam("type", type)
				.queryParam("data", data)
				.build())
				.header("Content-Type", "text/html")
				.retrieve().bodyToMono(Boolean.class);
	}
	
	public Mono<AtributoForCampo> getAtributoForCampoById(String id) {
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
	
	public Mono<String> getClassTypeOfFormat(String format){
		return webClientBuilder.build().get()
				.uri(clientProperties.getAtributoServiceUrl().concat("api/atributos/typeofformat/").concat(format))
				.header("Content-Type", "text/html")
				.retrieve().bodyToMono(String.class);
	}
	
	public Flux<AtributoForCampo> getAtributosByArrayOfIds(List<String> ids) { // TODO test
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("api/atributos/arrayofcampos/byids");
		if (ids.size()>0) {
			stringBuilder.append("?ids=");
		}
		ListIterator<String> iterator = ids.listIterator();
		if (iterator.hasNext()) {
			stringBuilder.append(iterator.next());
		}
		while (iterator.hasNext()) {
			stringBuilder.append(",");
			stringBuilder.append(iterator.next());
		}
		return webClientBuilder.build().get()
				.uri(clientProperties.getAtributoServiceUrl().concat(stringBuilder.toString()))
				.header("Content-Type", "text/html")
				.retrieve().bodyToFlux(AtributoForCampo.class);
	}
	
}
