package devs.mrp.gullproject.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Mono;

@FeignClient(name = "atributo-service")
public interface AtributoServiceProxy {

	@GetMapping("/api/atributos/data-validator")
	public Mono<Boolean> validateDataFormat(@RequestParam(name = "type") String type,
											@RequestParam(name = "data") String data);
	
	// TODO test
	// TODO obtener datos del Atributo
	/*@GetMapping()*/
	
}
