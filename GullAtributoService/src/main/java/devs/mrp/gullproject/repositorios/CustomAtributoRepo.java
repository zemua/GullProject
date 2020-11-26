package devs.mrp.gullproject.repositorios;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.Tipo;
import reactor.core.publisher.Mono;

public interface CustomAtributoRepo {

	public Mono<Atributo> pushAtributo(String id, Tipo tipo);
	
}
