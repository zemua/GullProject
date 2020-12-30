package devs.mrp.gullproject.service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.repository.CampoRepo;
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
		
		when(asp.getAtributoForCampoById(ArgumentMatchers.anyString())).thenReturn(Mono.just(otro));
		when(asp.getAtributoForCampoById(ArgumentMatchers.eq("idAtributo"))).thenReturn(Mono.just(afc));
		
		Boolean t = true;
		Boolean f = false;
		
		when(asp.validateDataFormat(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Mono.just(f));
		when(asp.validateDataFormat(ArgumentMatchers.eq("tipoAtributo"), ArgumentMatchers.eq("datos"))).thenReturn(Mono.just(t));
		
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

}
