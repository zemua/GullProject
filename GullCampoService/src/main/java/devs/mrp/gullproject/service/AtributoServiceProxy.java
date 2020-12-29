package devs.mrp.gullproject.service;

import org.reactivestreams.Publisher;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import devs.mrp.gullproject.domains.AtributoForCampo;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "atributo-service")
public interface AtributoServiceProxy {
	
	// TODO load balanced
	// TODO circuit breaker
	// TODO security
	
	// añadir wrapper reactivo
	// https://github.com/OpenFeign/feign/tree/master/reactive
	
	// o usar reactive feign
	// https://github.com/Playtika/feign-reactive

	@GetMapping("/api/atributos/data-validator")
	public Mono<Boolean> validateDataFormat(@RequestParam(name = "type") String type,
											@RequestParam(name = "data") String data);
	
	@GetMapping("/api/atributos/idforcampo/{id}")
	public Mono<AtributoForCampo> getAtributoForCampoById(@PathVariable(name = "id") String id);
	
}
