package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindException;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.dto.propuesta.AttRemaper;
import devs.mrp.gullproject.domains.dto.propuesta.AttRemapersWrapper;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import({LineaFactory.class, Consulta.class})
public class AttRemaperUtilitiesTest {

	@MockBean
	AtributoServiceProxyWebClient atributoService;
	@MockBean
	LineaService lineaService;
	
	AttRemaperUtilities attRemaperUtilities;
	
	Linea linea;
	Linea linea2;
	Linea lineab;
	Linea linea2b;
	Campo<String> campo1;
	Campo<String> campo1b;
	Campo<String> campo2;
	Campo<String> campo2b;
	AttRemaper rem1;
	AttRemaper rem2;
	AttRemapersWrapper remapers;
	BindException bindingResult;
	
	@Autowired
	AttRemaperUtilitiesTest(AttRemaperUtilities attRemaperUtilities) {
		this.attRemaperUtilities = attRemaperUtilities;
	}
	
	@BeforeEach
	void setup() {
		linea = new Linea();
		linea.setPropuestaId("propid");
		
		lineab = new Linea();
		lineab.setPropuestaId(linea.getPropuestaId());
		
		rem1 = new AttRemaper();
		rem1.setAfter("after1");
		rem1.setAfterObj("after1");
		rem1.setAtributoId("attId1");
		rem1.setBefore("before1");
		//rem1.setClase("String");
		rem1.setLocalIdentifier("localId1");
		rem1.setName("name1");
		rem1.setTipo("DESCRIPCION");
		
		campo1 = new Campo<>();
		campo1.setAtributoId(rem1.getAtributoId());
		campo1.setDatos(rem1.getBefore());
		
		campo1b = new Campo<>();
		campo1b.setAtributoId(rem1.getAtributoId());
		campo1b.setDatos(rem1.getAfter());
		
		rem2 = new AttRemaper();
		rem2.setAfter("after2");
		rem2.setAfterObj("after2");
		rem2.setAtributoId("attId1");
		rem2.setBefore("before2");
		//rem2.setClase("String");
		rem2.setLocalIdentifier("localId1");
		rem2.setName("name1");
		rem2.setTipo("DESCRIPCION");
		
		campo2 = new Campo<>();
		campo2.setAtributoId(rem2.getAtributoId());
		campo2.setDatos(rem2.getBefore());
		
		campo2b = new Campo<>();
		campo2b.setAtributoId(rem2.getAtributoId());
		campo2b.setDatos(rem2.getAfter());
		
		linea.getCampos().add(campo1);
		linea.getCampos().add(campo2);
		
		lineab.getCampos().add(campo1b);
		lineab.getCampos().add(campo2b);
		
		remapers = new AttRemapersWrapper();
		remapers.setRemapers(new ArrayList<>());
		remapers.getRemapers().add(rem1);
		remapers.getRemapers().add(rem2);
		
		bindingResult = new BindException(remapers, "remapers");
		
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(rem1.getTipo()), ArgumentMatchers.eq(rem1.getAfter()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(rem2.getTipo()), ArgumentMatchers.eq(rem2.getAfter()))).thenReturn(Mono.just(true));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq(rem1.getTipo()))).thenReturn(Mono.just("String"));
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(linea.getPropuestaId()))).thenReturn(Flux.just(linea));
		when(lineaService.updateLinea(ArgumentMatchers.any(Linea.class))).thenReturn(Mono.just(lineab));
	}
	
	@Test
	void testValidateAttRemapers() {
		Flux<Boolean> flux = attRemaperUtilities.validateAttRemapers(remapers.getRemapers(), bindingResult, "remapers");
		
		StepVerifier.create(flux)
			.assertNext(b -> assertTrue(b))
			.assertNext(b -> assertTrue(b))
			.expectComplete()
			.verify()
			;
	}
	
	@Test
	void testRemapLineasAtt() {
		Flux<Linea> lineas = attRemaperUtilities.remapLineasAtt(remapers.getRemapers(), linea.getPropuestaId());
		
		StepVerifier.create(lineas)
			.assertNext(l -> {
				assertEquals(rem1.getAfter(), l.getCampos().get(0).getDatosText());
			})
			.expectComplete()
			.verify()
			;
	}
	
}
