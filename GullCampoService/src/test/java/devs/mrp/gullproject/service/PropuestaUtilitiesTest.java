package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindException;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.dto.WrapAtributosForCampoDto;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PropuestaUtilitiesTest {

	@MockBean
	ConsultaService consultaService;
	
	PropuestaUtilities propuestaUtilities;
	
	@Autowired
	public PropuestaUtilitiesTest(PropuestaUtilities propuestaUtilities) {
		this.propuestaUtilities = propuestaUtilities;
	}
	
	Propuesta propuesta;
	AtributoForCampo att1;
	AtributoForCampo att2;
	WrapAtributosForCampoDto wrapAtributosForCampoDto;
	List<AtributoForCampo> listAtts;
	
	@BeforeEach
	void setup() {
		att1 = new AtributoForCampo();
		att1.setId("att1id");
		att1.setName("nameatt1");
		att1.setTipo("DESCRIPCION");
		att1.setOrder(0);
		att2 = new AtributoForCampo();
		att2.setId("att2id");
		att2.setName("nameatt2");
		att2.setTipo("DESCRIPCION");
		att2.setOrder(1);
		propuesta = new PropuestaCliente();
		propuesta.addAttribute(att1);
		propuesta.addAttribute(att2);
		
		listAtts = new ArrayList<>();
		listAtts.add(att1);
		listAtts.add(att2);
		
		wrapAtributosForCampoDto = new WrapAtributosForCampoDto();
		wrapAtributosForCampoDto.setAtributos(listAtts);
		
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(att1, att2));
	}
	
	@Test
	void testWrapAtributos() {
		Mono<WrapAtributosForCampoDto> wrap = propuestaUtilities.wrapAtributos(propuesta.getId());
		StepVerifier.create(wrap)
			.assertNext(w -> {
				assertEquals(att1.getLocalIdentifier(), w.getAtributos().get(0).getLocalIdentifier());
				assertEquals(att2.getLocalIdentifier(), w.getAtributos().get(1).getLocalIdentifier());
			})
			.expectComplete()
			.verify()
			;
	}
	
	@Test
	void testAtributosFromWrapAndValidateBelongsToPropuesta() {
		BindException bindingResult = new BindException(wrapAtributosForCampoDto, "wrapAtributosForCampoDto");
		
		log.debug("should be ok");
		AtributoForCampo atta = new AtributoForCampo();
		atta.setLocalIdentifier(att1.getLocalIdentifier());
		atta.setOrder(3);
		AtributoForCampo attb = new AtributoForCampo();
		attb.setLocalIdentifier(att2.getLocalIdentifier());
		attb.setOrder(1);
		List<AtributoForCampo> atts = new ArrayList<>();
		atts.add(atta);
		atts.add(attb);
		wrapAtributosForCampoDto.setAtributos(atts);
		Mono<List<AtributoForCampo>> list = propuestaUtilities.atributosOrderFromWrapAndValidateBelongsToPropuesta(wrapAtributosForCampoDto, bindingResult, propuesta.getId());
		StepVerifier.create(list)
			.assertNext(l -> {
				assertTrue(!bindingResult.hasErrors());
				assertEquals(att1.getId(), l.get(0).getId());
				assertEquals(att1.getName(), l.get(0).getName());
				assertEquals(att1.getTipo(), l.get(0).getTipo());
				assertEquals(atta.getOrder(), l.get(0).getOrder());
				assertEquals(att2.getId(), l.get(1).getId());
				assertEquals(att2.getName(), l.get(1).getName());
				assertEquals(att2.getTipo(), l.get(1).getTipo());
				assertEquals(attb.getOrder(), l.get(1).getOrder());
			})
			.expectComplete()
			.verify()
			;
		
		log.debug("should be error");
		wrapAtributosForCampoDto.getAtributos().get(0).setLocalIdentifier("incorrect");
		list = propuestaUtilities.atributosOrderFromWrapAndValidateBelongsToPropuesta(wrapAtributosForCampoDto, bindingResult, propuesta.getId());
		StepVerifier.create(list)
			.assertNext(l -> {
				assertTrue(bindingResult.hasErrors());
				assertEquals(1, l.size());
				assertEquals(att2.getId(), l.get(0).getId());
				assertEquals(att2.getName(), l.get(0).getName());
				assertEquals(att2.getTipo(), l.get(0).getTipo());
				assertEquals(attb.getOrder(), l.get(0).getOrder());
			})
			.expectComplete()
			.verify()
			;
	}
	
}
