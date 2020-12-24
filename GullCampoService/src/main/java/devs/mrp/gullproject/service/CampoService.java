package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.repository.CampoRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CampoService {	
	
	private CampoRepo campoRepo;
	private AtributoServiceProxy asp;
	
	@Autowired
	public CampoService(CampoRepo campoRepo, AtributoServiceProxy asp) {
		this.campoRepo = campoRepo;
		this.asp = asp;
	}
	
	public Mono<Campo<?>> findById(String id) {
		return campoRepo.findById(id);
	}
	
	public Flux<Campo<?>> findAll(){
		return campoRepo.findAll();
	}
	
	// TODO test
	public Mono<Boolean> validateDataFormat(Campo<?> campo) {
		Mono<AtributoForCampo> afc = Mono.just(asp.getAtributoForCampoById(campo.getAtributoId()));
		Mono<Boolean> validated = Mono.just(asp.validateDataFormat(afc.block().getTipo(), campo.getDatos().toString()));
				
		//return validated;
		return null;
	}

}
