package devs.mrp.gullproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.repositorios.AtributoRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AtributoService {
	
	private AtributoRepo atributoRepo;
	
	@Autowired
	public AtributoService(AtributoRepo atributoRepo) {
		this.atributoRepo = atributoRepo;
	}
	
	public Flux<Atributo> findAll() {
		return atributoRepo.findAll();
	}
	
	public Mono<Atributo> findById(String id){
		return atributoRepo.findById(id);
	}
	
	public Mono<Atributo> save(Atributo a) {
		return atributoRepo.save(a);
	}
	
	public Mono<Void> deleteById(String a) {
		return atributoRepo.deleteById(a);
	}
	
	public Mono<Long> getCount() {
		return atributoRepo.count();
	}
	
	public Mono<Atributo> updateName(String id, String name) {
		return atributoRepo.updateNameOfAtributo(id, name);
	}
	
	public Flux<Atributo> findAtributoByIdIn(List<String> ids) {
		return atributoRepo.findAtributosByIdIn(ids);
	}

}
