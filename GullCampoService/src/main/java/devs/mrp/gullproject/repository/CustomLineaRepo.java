package devs.mrp.gullproject.repository;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomLineaRepo {

	public Mono<Linea> addCampo(String idLinea, Campo<?> campo);
	
	public Mono<Long> addVariosCampos(String idLinea, Flux<Campo<?>> campos);
	
	public Mono<Linea> removeCampo(String idLinea, Campo<?> campo);
	
	public Mono<Linea> removeVariosCampos(String idLinea, Campo<?>[] campos);
	
	public Mono<Linea> updateCampo(String idLinea, Campo<?> campo);
	
	public Mono<Long> updateVariosCampos(String idLinea, Flux<Campo<?>> campo);
	
	public Mono<Linea> updateNombre(String idLinea, String nombre);
	
	public Mono<Linea> updateOrder(String idLinea, Integer order);
	
}
