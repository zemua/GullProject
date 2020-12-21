package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.repository.CampoRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CampoService {	
	
	private CampoRepo campoRepo;
	
	@Autowired
	public CampoService(CampoRepo campoRepo) {
		this.campoRepo = campoRepo;
	}
	
	public Mono<Campo<?>> findById(String id) {
		return campoRepo.findById(id);
	}
	
	public Flux<Campo<?>> findAll(){
		return campoRepo.findAll();
	}

}
