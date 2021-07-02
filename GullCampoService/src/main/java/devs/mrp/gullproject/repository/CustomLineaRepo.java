package devs.mrp.gullproject.repository;


import java.util.List;

import com.mongodb.client.result.DeleteResult;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.PvperLinea;
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
	
	public Mono<Linea> updateParentId(String idLinea, String parentId);
	
	public Mono<Linea> updateCounterLineId(String idLinea, List<String> counterLineId);
	
	public Mono<Linea> addCounterLineId(String idLinea, String counterLineId);
	
	public Mono<Linea> removeCounterLineId(String idLinea, String counterLineId);
	
	public Mono<DeleteResult> deleteSeveralLineasByPropuestaId(String propuestaId);
	
	public Mono<DeleteResult> deleteSeveralLineasBySeveralPropuestaIds(List<String> propuestaIds);
	
	public Mono<Linea> updatePvps(String idLinea, List<PvperLinea> pvps);
	
	public Mono<Linea> updateCosts(String idLinea, List<CosteLineaProveedor> costs);
	
}
