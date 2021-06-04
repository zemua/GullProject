package devs.mrp.gullproject.repositorios;

import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Atributo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomAtributoRepo {
	
	public Mono<Atributo> updateNameOfAtributo(String id, String name);
	
	public Mono<Atributo> updateOrdenOfAtributo(String id, Integer orden);
	
}
