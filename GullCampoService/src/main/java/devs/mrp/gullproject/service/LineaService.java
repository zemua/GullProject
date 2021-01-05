package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
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
	
}
