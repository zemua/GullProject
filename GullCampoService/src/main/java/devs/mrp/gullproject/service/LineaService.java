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
	
	public Mono<Linea> addCampo(String id, Campo<?> campo){
		return lineaRepo.addCampo(id, campo);
	}
	
	public Mono<Linea> removeCampo(String id, Campo<?> campo){
		return lineaRepo.removeCampo(id, campo);
	}
	
	public Mono<Linea> addLinea(Linea linea) {
		return lineaRepo.insert(linea);
	}
	
	public Mono<Linea> updateLinea(Linea linea) {
		return lineaRepo.save(linea);
	}
	
	public Mono<Long> deleteById(String id) { // TODO test
		return lineaRepo.deleteByIdReturningDeletedCount(id);
	}
	
}
