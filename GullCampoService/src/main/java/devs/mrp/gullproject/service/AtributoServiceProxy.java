package devs.mrp.gullproject.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import devs.mrp.gullproject.domains.AtributoForCampo;
import reactor.core.publisher.Mono;

@FeignClient(name = "atributo-service")
public interface AtributoServiceProxy {
	
	// TODO test
	
	// añadir wrapper reactivo
	// https://github.com/OpenFeign/feign/tree/master/reactive
	
	// o usar reactive feign
	// https://github.com/Playtika/feign-reactive

	@GetMapping("/api/atributos/data-validator")
	public Boolean validateDataFormat(@RequestParam(name = "type") String type,
											@RequestParam(name = "data") String data);
	
	@GetMapping("/api/atributos/idforcampo/{id}")
	public AtributoForCampo getAtributoForCampoById(@RequestParam(name = "id") String id);
	
}
