package devs.mrp.gullproject.repositorios;

import devs.mrp.gullproject.domains.Atributo;
import reactor.core.publisher.Mono;

public interface CustomAtributoRepo {

	public void pushAtributo(String id, String name, Atributo.dataFormat dataFormat);
	
	public void pushAtributo(Mono<String> id, Mono<String> name, Mono<Atributo.dataFormat> dataFormat);
	
}
