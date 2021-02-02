package devs.mrp.gullproject.service;

import java.util.List;

import org.reactivestreams.Publisher;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.StringWrapper;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "atributo-service")
public interface AtributoServiceProxy {
	
	// TODO UTILIZAR WEBCLIENT EN LUGAR DE FEIGN!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	// añadir wrapper reactivo
	// https://github.com/OpenFeign/feign/tree/master/reactive
	
	// o usar reactive feign
	// https://github.com/Playtika/feign-reactive

	@GetMapping("/api/atributos/data-validator")
	public Mono<Boolean> validateDataFormat(@RequestParam(name = "type") String type,
											@RequestParam(name = "data") String data);
	
	@GetMapping("/api/atributos/idforcampo/{id}")
	public Mono<AtributoForCampo> getAtributoForCampoById(@PathVariable(name = "id") String id);
	
	@GetMapping("/api/atributos/formatos")
	public Flux<StringWrapper> getTodosLosDataFormat();
	
}
