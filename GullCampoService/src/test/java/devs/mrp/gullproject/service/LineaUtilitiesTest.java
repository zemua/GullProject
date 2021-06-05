package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.dto.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.LineaWithAttListDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LineaUtilitiesTest {
	
	ModelMapper modelMapper;
	LineaUtilities lineaUtilities;
	
	@MockBean
	ConsultaService consultaService;
	@MockBean
	AtributoServiceProxyWebClient atributoService;
	@MockBean
	BindingResult bindingResult;
	
	AtributoForCampo att1;
	AtributoForCampo att2;
	Campo<String> campo1;
	Campo<String> campo2;
	Linea linea1;
	Propuesta propuesta;
	
	@Autowired
	LineaUtilitiesTest(ModelMapper modelMapper, LineaUtilities lineaUtilities) {
		this.modelMapper = modelMapper;
		this.lineaUtilities = lineaUtilities;
	}
	
	@BeforeEach
	void setup() {
		propuesta = new PropuestaCliente();
		att1 = new AtributoForCampo();
		att1.setId("id1");
		att1.setName("atributo1");
		att1.setTipo("DESCRIPCION");
		att2 = new AtributoForCampo();
		att2.setId("id2");
		att2.setName("atributo2");
		att2.setTipo("DESCRIPCION");
		campo1 = new Campo<>();
		campo1.setAtributoId(att1.getId());
		campo1.setDatos("datos1");
		campo2 = new Campo<>();
		campo2.setAtributoId(att2.getId());
		campo2.setDatos("datos2");
		linea1 = new Linea();
		linea1.setNombre("nombre linea");
		linea1.setPropuestaId(propuesta.getId());
		linea1.addCampo(campo1);
		linea1.addCampo(campo2);
		propuesta.addLineaId(linea1.getId());
		propuesta.addAttribute(att1);
		propuesta.addAttribute(att2);
	}
	
	@Test
	void testGetAttributesOfProposal() {
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(att1,att2));
		Mono<LineaWithAttListDto> mono = lineaUtilities.getAttributesOfProposal(linea1, propuesta.getId());
		StepVerifier.create(mono)
			.assertNext(dto -> {
				assertEquals(linea1, dto.getLinea());
				assertEquals(att1.getId(), dto.getAttributes().get(0).getId());
				assertEquals(att2.getName(), dto.getAttributes().get(1).getName());
			})
			.expectComplete()
			.verify()
			;
	}
	
	@Test
	void testAssertBindingResultOfListDto() {
		when(atributoService.validateDataFormat(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(false));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq("DESCRIPCION"), ArgumentMatchers.eq("valor1"))).thenReturn(Mono.just(true));
		AtributoForLineaFormDto a1 = modelMapper.map(att1, AtributoForLineaFormDto.class);
		a1.setValue("valor1");
		AtributoForLineaFormDto a2 = modelMapper.map(att2, AtributoForLineaFormDto.class);
		a2.setValue("valor2");
		List<AtributoForLineaFormDto> as = new ArrayList<>();
		as.add(a1);
		as.add(a2);
		LineaWithAttListDto dto = new LineaWithAttListDto(linea1, as);
		Flux<Boolean> assertions = lineaUtilities.assertBindingResultOfListDto(dto, bindingResult);
		
		StepVerifier.create(assertions)
			.assertNext(b -> {
				assertTrue(b);
			})
			.assertNext(b -> {
				assertTrue(!b);
			})
			.expectComplete()
			.verify()
			;
	}

}
