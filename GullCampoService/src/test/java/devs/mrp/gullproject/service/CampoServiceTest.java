package devs.mrp.gullproject.service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.repository.CampoRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CampoServiceTest {
	
	@Autowired
	CampoService campoService;
	
	@MockBean
	AtributoServiceProxy asp;
	@MockBean
	CampoRepo campoRepo;

	@Test
	void testValidateDataFormat() {
		
		AtributoForCampo afc = new AtributoForCampo();
		afc.setId("idAtributo");
		afc.setName("nombreAtributo");
		afc.setTipo("tipoAtributo");
		
		AtributoForCampo otro = new AtributoForCampo();
		otro.setId("otroIdAtributo");
		otro.setName("otroName");
		otro.setTipo("otroTipo");
		
		// TODO implementar una vez hayamos pasado de reactiveFeign a WebClient
		//when(asp.getAtributoForCampoById(ArgumentMatchers.anyString())).thenReturn(Mono.just(otro));
		//when(asp.getAtributoForCampoById(ArgumentMatchers.eq("idAtributo"))).thenReturn(Mono.just(afc));
		
		Boolean t = true;
		Boolean f = false;
		
		//when(asp.validateDataFormat(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Mono.just(f));
		//when(asp.validateDataFormat(ArgumentMatchers.eq("tipoAtributo"), ArgumentMatchers.eq("datos"))).thenReturn(Mono.just(t));
		
		Campo<String> campo = new Campo<>();
		campo.setAtributoId("idAtributo");
		campo.setDatos("datos");
		campo.setId("idCampo");
		
		Mono<Boolean> mono = campoService.validateDataFormat(campo);
		StepVerifier
			.create(mono)
			.expectNext(true)
			.verifyComplete();
		
		Campo<String> ca = new Campo<>();
		ca.setAtributoId("idAtributo");
		ca.setDatos("incorrectos");
		ca.setId("idCampo");
		
		Mono<Boolean> monoCa = campoService.validateDataFormat(ca);
		StepVerifier
			.create(monoCa)
			.expectNext(false)
			.verifyComplete();
		
		Campo<String> cb = new Campo<>();
		cb.setAtributoId("idIncorrecto");
		cb.setDatos("datos");
		cb.setId("idCampo");
		
		Mono<Boolean> monoCb = campoService.validateDataFormat(cb);
		StepVerifier
			.create(monoCb)
			.expectNext(false)
			.verifyComplete();
	}
	
	@Test
	void testAnhadirUno() {
		
		Campo<String> campo = new Campo<>();
		campo.setAtributoId("idAtributo");
		campo.setDatos("datos");
		campo.setId("idCampo");
		
		when(campoRepo.insert(ArgumentMatchers.any(Campo.class))).thenReturn(Mono.just(campo));
		when(campoRepo.insert(ArgumentMatchers.eq(campo))).thenReturn(Mono.just(campo));
		
		Mono<Campo<?>> mono = campoService.anhadirUno(campo);
		StepVerifier
			.create(mono)
			.expectNext(campo)
			.verifyComplete();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testAnhadirVarios() {
		
		Campo<String> campo1 = new Campo<>();
		campo1.setAtributoId("idAtributo");
		campo1.setDatos("datos");
		campo1.setId("idCampo");
		
		Campo<Integer> campo2 = new Campo<>();
		campo2.setAtributoId("idAtributo2");
		campo2.setDatos(1234);
		campo2.setId("idCampo2");
		
		Flux<Campo<?>> flux = Flux.just(campo1, campo2);
		
		when(campoRepo.insert(ArgumentMatchers.any(Publisher.class))).thenReturn(Flux.just());
		when(campoRepo.insert(ArgumentMatchers.eq(flux))).thenReturn(flux);
		
		Flux<Campo<?>> response = campoService.anhadirVarios(flux);
		StepVerifier
			.create(response)
			.expectNext(campo1)
			.expectNext(campo2)
			.verifyComplete();
	}
	
	@Test
	void testActualizarUno() {
		
		Campo<String> campo1 = new Campo<>();
		campo1.setAtributoId("idAtributo1");
		campo1.setDatos("datos1");
		campo1.setId("idCampo1");
		
		Campo<Integer> campo2 = new Campo<>();
		campo2.setAtributoId("idAtributo2");
		campo2.setDatos(1234);
		campo2.setId("idCampo2");
		
		when(campoRepo.save(ArgumentMatchers.any(Campo.class))).thenReturn(Mono.just(campo2));
		when(campoRepo.save(ArgumentMatchers.eq(campo1))).thenReturn(Mono.just(campo1));
		
		Mono<Campo<?>> response = campoService.actualizarUno(campo1);
		StepVerifier
			.create(response)
			.expectNext(campo1)
			.verifyComplete();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testActualizarVarios() {
		
		Campo<String> campo1 = new Campo<>();
		campo1.setAtributoId("idAtributo1");
		campo1.setDatos("datos1");
		campo1.setId("idCampo1");
		
		Campo<Integer> campo2 = new Campo<>();
		campo2.setAtributoId("idAtributo2");
		campo2.setDatos(1234);
		campo2.setId("idCampo2");
		
		Flux<Campo<?>> flux = Flux.just(campo1, campo2);
		
		when(campoRepo.saveAll(ArgumentMatchers.any(Publisher.class))).thenReturn(Flux.just());
		when(campoRepo.saveAll(ArgumentMatchers.eq(flux))).thenReturn(flux);
		
		Flux<Campo<?>> response = campoService.actualizarVarios(flux);
		StepVerifier
			.create(response)
			.expectNext(campo1)
			.expectNext(campo2)
			.verifyComplete();
	}
	
	@Test
	void testBorrarUno() {
		
		Campo<String> campo1 = new Campo<>();
		campo1.setAtributoId("idAtributo1");
		campo1.setDatos("datos1");
		campo1.setId("idCampo1");
		
		Campo<Integer> campo2 = new Campo<>();
		campo2.setAtributoId("idAtributo2");
		campo2.setDatos(1234);
		campo2.setId("idCampo2");
		
		Mono<Long> mono = Mono.just(Long.valueOf(1));
		
		when(campoRepo.deleteByIdReturningDeletedCount(ArgumentMatchers.any(String.class))).thenReturn(Mono.just(Long.valueOf(0)));
		when(campoRepo.deleteByIdReturningDeletedCount(ArgumentMatchers.eq(campo1.getId()))).thenReturn(mono);
		
		Mono<Long> response = campoService.borrarUno(campo1.getId());
		StepVerifier
			.create(response)
			.expectNext(Long.valueOf(1))
			.verifyComplete();
	}
	
	@Test
	void testBorrarVarios() {
		
		Campo<String> campo1 = new Campo<>();
		campo1.setAtributoId("idAtributo1");
		campo1.setDatos("datos1");
		campo1.setId("idCampo1");
		
		Campo<Integer> campo2 = new Campo<>();
		campo2.setAtributoId("idAtributo2");
		campo2.setDatos(1234);
		campo2.setId("idCampo2");
		
		Flux<String> flux = Flux.just(campo1, campo2).map(m -> m.getId());
		
		when(campoRepo.deleteByIdReturningDeletedCount(ArgumentMatchers.any(String.class))).thenReturn(Mono.just(Long.valueOf(0)));
		when(campoRepo.deleteByIdReturningDeletedCount(ArgumentMatchers.eq(campo1.getId()))).thenReturn(Mono.just(Long.valueOf(1)));
		when(campoRepo.deleteByIdReturningDeletedCount(ArgumentMatchers.eq(campo2.getId()))).thenReturn(Mono.just(Long.valueOf(1)));
		
		Flux<Long> response = campoService.borrarVarios(flux);
		StepVerifier
			.create(response)
			.expectNext(Long.valueOf(1))
			.expectNext(Long.valueOf(1))
			.verifyComplete();
	}

}
