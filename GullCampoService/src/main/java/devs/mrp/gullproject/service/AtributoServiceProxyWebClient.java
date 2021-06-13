package devs.mrp.gullproject.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import devs.mrp.gullproject.configuration.ClientProperties;
import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.StringWrapper;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AtributoServiceProxyWebClient {
	
	WebClient.Builder webClientBuilder;
	ClientProperties clientProperties;
	ProxyUtils proxyUtils;
	
	@Autowired
	public AtributoServiceProxyWebClient(WebClient.Builder webClientBuilder, ClientProperties clientProperties, ProxyUtils proxyUtils) {
		this.webClientBuilder = webClientBuilder;
		this.clientProperties = clientProperties;
		this.proxyUtils = proxyUtils;
	}
	
	public Mono<Boolean> validateDataFormat(String type, String data){
		try {
			type = URLEncoder.encode(type, StandardCharsets.UTF_8.toString());
			data = URLEncoder.encode(data, StandardCharsets.UTF_8.toString());
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return webClientBuilder.build().get()
				.uri(clientProperties.getAtributoServiceUrl() + "api/atributos/data-validator" + "?type=" + type + "&data=" + data) // not so clean, but can be tested easier
				/*.uri(uriBuilder -> uriBuilder
					.host(clientProperties.getAtributoServiceHost()) // works well, but breaks the tests
					.path("api/atributos/data-validator")
					.queryParam("type", type)
					.queryParam("data", data)
					.build())*/
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
		log.debug("calling atributoService asking for " + format);
		return webClientBuilder.build().get()
				.uri(clientProperties.getAtributoServiceUrl().concat("api/atributos/typeofformat/").concat(format))
				.header("Content-Type", "text/html")
				.retrieve().bodyToMono(String.class);
	}
	
	public Flux<AtributoForCampo> getAtributosByArrayOfIds(List<String> ids) {
		if (ids.size() == 0) {
			return Flux.empty();
		}
		//List<String> params = new ArrayList<>();
		/*ListIterator<String> iterator = ids.listIterator();
		while (iterator.hasNext()) {
			//params.add(iterator.next());
		}*/
		String params = proxyUtils.UrlfyListOfParameter("ids", ids);
		return webClientBuilder.build().get()
				.uri(clientProperties.getAtributoServiceUrl() + "api/atributos/arrayofcampos/byids" + params)
				/*.uri(uriBuilder -> uriBuilder
						.host(clientProperties.getAtributoServiceHost()) // works great, but breaks the testing
						.path("api/atributos/arrayofcampos/byids")
						.queryParam("ids", params)
						.build())*/
				.header("Content-Type", "text/html")
				.retrieve().bodyToFlux(AtributoForCampo.class);
	}
	
}
