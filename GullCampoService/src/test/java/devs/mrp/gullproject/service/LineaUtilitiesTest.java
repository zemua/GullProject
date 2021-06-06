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
import devs.mrp.gullproject.domains.StringListOfListsWrapper;
import devs.mrp.gullproject.domains.StringListWrapper;
import devs.mrp.gullproject.domains.dto.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.LineaWithAttListDto;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
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
	Campo<String> campo3;
	Campo<String> campo4;
	Linea linea2;
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
		linea1.setNombre("nombre linea 1");
		linea1.setPropuestaId(propuesta.getId());
		linea1.addCampo(campo1);
		linea1.addCampo(campo2);
		campo3 = new Campo<>();
		campo3.setAtributoId(att1.getId());
		campo3.setDatos("datos3");
		campo4 = new Campo<>();
		campo4.setAtributoId(att2.getId());
		campo4.setDatos("datos4");
		linea2 = new Linea();
		linea2.setNombre("nombre linea 2");
		linea2.setPropuestaId(propuesta.getId());
		linea2.addCampo(campo3);
		linea2.addCampo(campo4);
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
	
	@Test
	void testReconstructLine() {
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.anyString())).thenReturn(Mono.just("Integer"));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq(att1.getTipo()))).thenReturn(Mono.just("String"));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq(att2.getTipo()))).thenReturn(Mono.just("String"));
		
		AtributoForLineaFormDto a1 = modelMapper.map(att1, AtributoForLineaFormDto.class);
		a1.setValue("valor1");
		AtributoForLineaFormDto a2 = modelMapper.map(att2, AtributoForLineaFormDto.class);
		a2.setValue("valor2");
		List<AtributoForLineaFormDto> as = new ArrayList<>();
		as.add(a1);
		as.add(a2);
		LineaWithAttListDto dto = new LineaWithAttListDto(linea1, as);
		
		Mono<Linea> li = lineaUtilities.reconstructLine(dto);
		StepVerifier.create(li)
		.assertNext(rL -> {
			assertEquals(linea1.getNombre(), rL.getNombre());
			assertEquals(2, rL.getCantidadCampos());
			assertEquals(att1.getId(), rL.getCampoByIndex(0).getAtributoId());
			assertEquals(a2.getValue(), rL.getCampoByIndex(1).getDatosText());
		})
		.expectComplete()
		.verify()
		;
	}
	
	@Test
	void testExcelTextToLineObject() {
		String texto = "aaa	bbb	ccc\n"
				+ "111	222	333\n"
				+ "";
		
		StringListOfListsWrapper wrapper = lineaUtilities.excelTextToLineObject(texto);
		assertEquals("aaa", wrapper.getList(0).get(0));
		assertEquals("bbb", wrapper.getList(0).get(1));
		assertEquals("ccc", wrapper.getList(0).get(2));
		assertEquals("111", wrapper.getList(1).get(0));
		assertEquals("222", wrapper.getList(1).get(1));
		assertEquals("333", wrapper.getList(1).get(2));
	}
	
	@Test
	void testAddBulkTableErrorsToBindingResult() throws Exception {
		when(atributoService.validateDataFormat(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(false));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq("DESCRIPCION"), ArgumentMatchers.anyString())).thenReturn(Mono.just(true));
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(att1,att2));
		
		StringListOfListsWrapper wrapper = new StringListOfListsWrapper();
		
		List<Integer> names = new ArrayList<>();
		names.add(1);
		names.add(null);
		wrapper.setName(names);
		
		List<String> nAtts = new ArrayList<>();
		nAtts.add(att1.getId());
		nAtts.add(att2.getId());
		wrapper.setStrings(nAtts);
		
		List<StringListWrapper> wr = new ArrayList<>();
		StringListWrapper w1 = new StringListWrapper();
		w1.setString(new ArrayList<>());
		w1.add(campo1.getDatosText());
		w1.add(campo2.getDatosText());
		wr.add(w1);
		var w2 = new StringListWrapper();
		w2.setString(new ArrayList<>());
		w2.add(campo3.getDatosText());
		w2.add(campo4.getDatosText());
		wr.add(w2);
		wrapper.setStringListWrapper(wr);
		
		Flux<Boolean> assertions = lineaUtilities.addBulkTableErrorsToBindingResult(wrapper, propuesta.getId(), bindingResult);
		StepVerifier.create(assertions)
		.assertNext(b -> {
			assertTrue(b);
		})
		.assertNext(b -> {
			assertTrue(b);
		})
		.assertNext(b -> {
			assertTrue(b);
		})
		.assertNext(b -> {
			assertTrue(b);
		})
		.assertNext(b -> {
			assertTrue(b);
		})
		.expectComplete()
		.verify()
		;
		
		when(atributoService.validateDataFormat(ArgumentMatchers.eq("DESCRIPCION"), ArgumentMatchers.eq(campo4.getDatosText()))).thenReturn(Mono.just(false));
		assertions = lineaUtilities.addBulkTableErrorsToBindingResult(wrapper, propuesta.getId(), bindingResult);
		StepVerifier.create(assertions)
		.assertNext(b -> {
			assertTrue(b);
		})
		.assertNext(b -> {
			assertTrue(b);
		})
		.assertNext(b -> {
			assertTrue(b);
		})
		.assertNext(b -> {
			assertTrue(!b);
		})
		.assertNext(b -> {
			assertTrue(b);
		})
		.expectComplete()
		.verify()
		;
		
		when(atributoService.validateDataFormat(ArgumentMatchers.eq("DESCRIPCION"), ArgumentMatchers.anyString())).thenReturn(Mono.just(true));
		names = new ArrayList<>();
		names.add(null);
		names.add(null);
		wrapper.setName(names);
		
		assertions = lineaUtilities.addBulkTableErrorsToBindingResult(wrapper, propuesta.getId(), bindingResult);
		StepVerifier.create(assertions)
		.assertNext(b -> {
			assertTrue(b);
		})
		.assertNext(b -> {
			assertTrue(b);
		})
		.assertNext(b -> {
			assertTrue(b);
		})
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
	
	@Test
	void testAllLineasFromBulkWrapper() throws Exception {
		when(atributoService.validateDataFormat(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(false));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq("DESCRIPCION"), ArgumentMatchers.anyString())).thenReturn(Mono.just(true));
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(att1,att2));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.anyString())).thenReturn(Mono.just("Integer"));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq(att1.getTipo()))).thenReturn(Mono.just("String"));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq(att2.getTipo()))).thenReturn(Mono.just("String"));
		
		StringListOfListsWrapper wrapper = new StringListOfListsWrapper();
		
		List<Integer> names = new ArrayList<>();
		names.add(1);
		names.add(null);
		wrapper.setName(names);
		
		List<String> nAtts = new ArrayList<>();
		nAtts.add(att1.getId());
		nAtts.add(att2.getId());
		wrapper.setStrings(nAtts);
		
		List<StringListWrapper> wr = new ArrayList<>();
		StringListWrapper w1 = new StringListWrapper();
		w1.setString(new ArrayList<>());
		w1.add(campo1.getDatosText());
		w1.add(campo2.getDatosText());
		wr.add(w1);
		var w2 = new StringListWrapper();
		w2.setString(new ArrayList<>());
		w2.add(campo3.getDatosText());
		w2.add(campo4.getDatosText());
		wr.add(w2);
		wrapper.setStringListWrapper(wr);
		
		Mono<List<Linea>> mono = lineaUtilities.allLineasFromBulkWrapper(wrapper, propuesta.getId());
		StepVerifier.create(mono)
		.assertNext(m -> {
			assertEquals(att1.getId(), m.get(0).getCampoByIndex(0).getAtributoId());
			assertEquals(campo1.getDatosText(), m.get(0).getCampoByIndex(0).getDatosText());
			assertEquals(att2.getId(), m.get(0).getCampoByIndex(1).getAtributoId());
			assertEquals(campo2.getDatosText(), m.get(0).getCampoByIndex(1).getDatosText());
			assertEquals(campo1.getDatosText(), m.get(0).getNombre());
			assertEquals(att1.getId(), m.get(1).getCampoByIndex(0).getAtributoId());
			assertEquals(campo3.getDatosText(), m.get(1).getCampoByIndex(0).getDatosText());
			assertEquals(att2.getId(), m.get(1).getCampoByIndex(1).getAtributoId());
			assertEquals(campo4.getDatosText(), m.get(1).getCampoByIndex(1).getDatosText());
			assertEquals(campo3.getDatosText(), m.get(1).getNombre());
		})
		.expectComplete()
		.verify()
		;
	}

}
