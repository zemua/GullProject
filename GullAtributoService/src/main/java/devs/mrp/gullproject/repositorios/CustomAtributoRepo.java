package devs.mrp.gullproject.repositorios;

import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.Tipo;
import reactor.core.publisher.Mono;

public interface CustomAtributoRepo {
	
	public Mono<Atributo> updateNameOfAtributo(String id, String name);
	
}
