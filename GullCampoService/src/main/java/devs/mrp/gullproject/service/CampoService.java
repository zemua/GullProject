package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.repository.CampoRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CampoService {	
	
	private CampoRepo repo;
	
	@Autowired
	public CampoService(CampoRepo repo) {
		this.repo = repo;
	}
	
	public Mono<Campo<?>> findById(String id) {
		return repo.findById(id);
	}
	
	public Flux<Campo<?>> findAll(){
		return repo.findAll();
	}

}
