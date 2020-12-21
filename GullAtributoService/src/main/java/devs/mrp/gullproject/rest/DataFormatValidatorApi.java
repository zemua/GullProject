package devs.mrp.gullproject.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.DataFormat;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(value = "/api/atributos", produces = MediaType.APPLICATION_JSON_VALUE)
public class DataFormatValidatorApi {

	@GetMapping(path = "/data-validator")
	public Mono<Boolean> validateDataFormat(@RequestParam(name = "type") String type,
											@RequestParam(name = "data") String data){
		boolean b = false;
		try {
			DataFormat df = DataFormat.valueOf(type);
			b = df.checkIfValidValue(data);
		} catch (IllegalArgumentException e) {
			log.debug("valor del tipo no encaja con ninguna de las constantes válidas");
			b = false;
		} catch (NullPointerException e) {
			log.debug("valor del tipo es nulo");
			b = false;
		}
		return Mono.just(b);
	}
	
}
