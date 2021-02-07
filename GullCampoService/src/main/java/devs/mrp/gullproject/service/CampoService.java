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
	private AtributoServiceProxy atributoServiceProxy;
	
	@Autowired
	public CampoService(CampoRepo campoRepo, AtributoServiceProxy atributoServiceProxy) {
		this.campoRepo = campoRepo;
		this.atributoServiceProxy = atributoServiceProxy;
	}
	
	public Mono<Campo<?>> findById(String id) {
		return campoRepo.findById(id);
	}
	
	public Flux<Campo<?>> findAll(){
		return campoRepo.findAll();
	}
	
	public Mono<Boolean> validateDataFormat(Campo<?> campo) {
		// TODO implementar una vez hayamos cambiado a WebClient en lugar de reactiveFeign
		/*Mono<Boolean> afc = atributoServiceProxy
				.getAtributoForCampoById(campo.getAtributoId())
				.flatMap(m -> atributoServiceProxy.validateDataFormat(m.getTipo(), campo.getDatos().toString()));*/
				Mono<Boolean> afc = Mono.just(false);
		return afc;
	}
	
	public Mono<Campo<?>> anhadirUno(Campo<?> campo) {
		return campoRepo.insert(campo);
	}
	
	public Flux<Campo<?>> anhadirVarios(Flux<Campo<?>> campos){
		return campoRepo.insert(campos);
	}
	
	public Mono<Campo<?>> actualizarUno(Campo<?> campo) {
		return campoRepo.save(campo);
	}
	
	public Flux<Campo<?>> actualizarVarios(Flux<Campo<?>> campos) {
		return campoRepo.saveAll(campos);
	}
	
	public Mono<Long> borrarUno(String id) {
		return campoRepo.deleteByIdReturningDeletedCount(id);
	}
	
	public Flux<Long> borrarVarios(Flux<String> ids) {
		return ids.flatMap(m -> campoRepo.deleteByIdReturningDeletedCount(m));
	}

}
