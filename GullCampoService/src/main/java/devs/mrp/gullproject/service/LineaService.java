package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.repository.LineaRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LineaService {

	private LineaRepo lineaRepo;
	
	@Autowired
	public LineaService(LineaRepo lineaRepo) {
		this.lineaRepo = lineaRepo;
	}
	
	public Mono<Linea> findById(String id){
		return lineaRepo.findById(id);
	}
	
	public Flux<Linea> findByPropuestaId(String propuestaId) {
		return lineaRepo.findAllByPropuestaId(propuestaId);
	}
	
	public Flux<Linea> findAll(){
		return lineaRepo.findAll();
	}
	
	public Mono<Linea> addCampo(String idLinea, Campo<?> campo){
		return lineaRepo.addCampo(idLinea, campo);
	}
	
	public Mono<Long> addVariosCampos(String idLinea, Flux<Campo<?>> campos){		
		return lineaRepo.addVariosCampos(idLinea, campos);
	}
	
	public Mono<Linea> removeCampo(String idLinea, Campo<?> campo){
		return lineaRepo.removeCampo(idLinea, campo);
	}
	
	public Mono<Linea> removeVariosCampos(String idLinea, Campo<?>[] campos){
		return lineaRepo.removeVariosCampos(idLinea, campos);
	}
	
	public Mono<Linea> addLinea(Linea linea) {
		return lineaRepo.insert(linea);
	}
	
	public Flux<Linea> addVariasLineas(Flux<Linea> lineas) {
		return lineaRepo.insert(lineas);
	}
	
	public Mono<Linea> updateLinea(Linea linea) {
		return lineaRepo.save(linea);
	}
	
	public Flux<Linea> updateVariasLineas(Flux<Linea> lineas) {
		return lineaRepo.saveAll(lineas);
	}
	
	public Mono<Long> deleteLineaById(String id) {
		return lineaRepo.deleteByIdReturningDeletedCount(id);
	}
	
	public Mono<Long> deleteVariasLineasById(Flux<String> ids){
		return ids.flatMap(id -> lineaRepo.deleteById(id))
				.count();
	}
	
	public Mono<Void> deleteVariasLineas(Flux<Linea> lineas) {
		return lineaRepo.deleteAll(lineas);
	}
	
	public Mono<Void> updateOrderOfSeveralLineas(Map<String, Integer> idlineaVSposicion) {
		Set<String> set = idlineaVSposicion.keySet();
		set.stream().forEach(id -> {
			lineaRepo.updateOrder(id, idlineaVSposicion.get(id)).subscribe();
		});
		return Mono.empty();
	}
	
	public Mono<Long> deleteSeveralLineasFromPropuestaId(String propuestaId){
		return lineaRepo.deleteSeveralLineasByPropuestaId(propuestaId).map(r -> Long.valueOf(r.getDeletedCount()));
	}
	
	public Mono<Long> deleteSeveralLineasFromSeveralPropuestaIds(List<String> propuestaIds) {
		return lineaRepo.deleteSeveralLineasBySeveralPropuestaIds(propuestaIds).map(r-> Long.valueOf(r.getDeletedCount()));
	}
	
	public Mono<Long> deleteSeveralLineasFromSeveralPropuestas(List<Propuesta> propuestas) {
		List<String> ids = propuestas.stream().map(arg0 -> arg0.getId()).collect(Collectors.toList());
		return lineaRepo.deleteSeveralLineasBySeveralPropuestaIds(ids).map(r-> Long.valueOf(r.getDeletedCount()));
	}
	
}
