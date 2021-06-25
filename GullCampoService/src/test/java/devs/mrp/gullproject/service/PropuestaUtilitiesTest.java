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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindException;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AttributesListDto;
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
	@MockBean
	AtributoServiceProxyWebClient atributoService;
	
	PropuestaUtilities propuestaUtilities;
	ModelMapper modelMapper;
	
	@Autowired
	public PropuestaUtilitiesTest(PropuestaUtilities propuestaUtilities, ModelMapper modelMapper) {
		this.propuestaUtilities = propuestaUtilities;
		this.modelMapper = modelMapper;
	}
	
	Propuesta propuesta;
	AtributoForCampo att1;
	AtributoForCampo att2;
	WrapAtributosForCampoDto wrapAtributosForCampoDto;
	List<AtributoForCampo> listAtts;
	AtributoForCampo att3;
	
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
		propuesta.operations().addAttribute(att1);
		propuesta.operations().addAttribute(att2);
		
		att3 = new AtributoForCampo();
		att3.setId("att3id");
		att3.setName("nameatt3");
		att3.setTipo("DESCRIPCION");
		att3.setOrder(2);
		
		listAtts = new ArrayList<>();
		listAtts.add(att1);
		listAtts.add(att2);
		
		wrapAtributosForCampoDto = new WrapAtributosForCampoDto();
		wrapAtributosForCampoDto.setAtributos(listAtts);
		
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(att1, att2));
		when(consultaService.findPropuestaByPropuestaId(propuesta.getId())).thenReturn(Mono.just(propuesta));
		when(atributoService.getAllAtributos()).thenReturn(Flux.just(att1, att2, att3));
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
		
		AtributoForCampo atta = new AtributoForCampo();
		atta.setLocalIdentifier(att1.getLocalIdentifier());
		atta.setOrder(3);
		AtributoForCampo attb = new AtributoForCampo();
		attb.setLocalIdentifier(att2.getLocalIdentifier());
		attb.setOrder(1);
		List<AtributoForCampo> atts = new ArrayList<>();
		atts.add(atta);
		atts.add(attb);
		
		log.debug("should be ok");
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
	
	@Test
	void testGetAttributesAndMarkActualFromProposal() {
		Mono<AttributesListDto> listDto = propuestaUtilities.getAttributesAndMarkActualFromProposal(propuesta.getId());
		StepVerifier.create(listDto)
			.assertNext(lDto -> {
				assertEquals(3, lDto.getAttributes().size());
				assertEquals(att1.getId(), lDto.getAttributes().get(0).getId());
				assertEquals(true, lDto.getAttributes().get(0).getSelected());
				assertEquals(att2.getId(), lDto.getAttributes().get(1).getId());
				assertEquals(true, lDto.getAttributes().get(1).getSelected());
				assertEquals(att3.getId(), lDto.getAttributes().get(2).getId());
				assertEquals(false, lDto.getAttributes().get(2).getSelected());
			})
			.expectComplete()
			.verify()
			;
	}
	
	@Test
	void testListOfSelectedAttributes() {
		AtributoForFormDto dto1 = modelMapper.map(att1, AtributoForFormDto.class);
		dto1.setSelected(true);
		AtributoForFormDto dto2 = modelMapper.map(att2, AtributoForFormDto.class);
		dto2.setSelected(true);
		AtributoForFormDto dto3 = modelMapper.map(att3, AtributoForFormDto.class);
		dto3.setSelected(false);
		List<AtributoForFormDto> dtos = new ArrayList<>();
		dtos.add(dto1);
		dtos.add(dto2);
		dtos.add(dto3);
		AttributesListDto atts = new AttributesListDto(dtos);
		
		Flux<AtributoForCampo> fl = propuestaUtilities.listOfSelectedAttributes(atts);
		StepVerifier.create(fl)
			.assertNext(a -> {
				assertEquals(att1.getId(), a.getId());
			})
			.assertNext(a -> {
				assertEquals(att2.getId(), a.getId());
			})
			.expectComplete()
			.verify()
			;
	}
	
}
