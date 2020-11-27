package devs.mrp.gullproject.repositorios;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.Tipo;
import reactor.core.publisher.Mono;

public interface CustomAtributoRepo {
	
	// TODO test

	public Mono<Atributo> addAtributo(String id, Tipo tipo);
	
	public Mono<Atributo> removeAtributo(String id, Tipo tipo);
	
}
