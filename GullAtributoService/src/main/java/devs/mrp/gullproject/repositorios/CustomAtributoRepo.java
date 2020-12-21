package devs.mrp.gullproject.repositorios;

import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.Tipo;
import reactor.core.publisher.Mono;

public interface CustomAtributoRepo {
	
	// https://practicaldev-herokuapp-com.global.ssl.fastly.net/iuriimednikov/how-to-build-custom-queries-with-spring-data-reactive-mongodb-1802

	public Mono<Atributo> addAtributo(String id, Tipo tipo);
	
	public Mono<Atributo> removeAtributo(String id, Tipo tipo);
	
}
