package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.dto.StringListWrapper;
import devs.mrp.gullproject.domains.dto.linea.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.linea.LineaWithAttListDto;
import devs.mrp.gullproject.domains.dto.linea.StringListOfListsWrapper;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.service.linea.LineaOperations;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.linea.LineaUtilities;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import({LineaFactory.class, Consulta.class})
public class LineaUtilitiesTest {
	
	@Autowired LineaFactory lineaFactory;
	
	ModelMapper modelMapper;
	LineaUtilities lineaUtilities;
	
	@MockBean
	CompoundedConsultaLineaService compoundedService;
	@MockBean
	ConsultaService consultaService;
	@MockBean
	AtributoServiceProxyWebClient atributoService;
	@MockBean
	BindingResult bindingResult;
	@MockBean
	LineaService lineaService;
	
	AtributoForCampo att1;
	AtributoForCampo att2;
	Campo<String> campo1;
	Campo<String> campo2;
	Linea linea1;
	LineaOperations linea1o;
	Campo<String> campo3;
	Campo<String> campo4;
	Linea linea2;
	LineaOperations linea2o;
	Propuesta propuesta;
	LineaWithAttListDto lineaWithAttListDto;
	List<Linea> lineas;
	
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
		linea1 = lineaFactory.create();
		linea1o = new LineaOperations(linea1);
		linea1.setNombre("nombre linea 1");
		linea1.setPropuestaId(propuesta.getId());
		linea1o.addCampo(campo1);
		linea1o.addCampo(campo2);
		campo3 = new Campo<>();
		campo3.setAtributoId(att1.getId());
		campo3.setDatos("datos3");
		campo4 = new Campo<>();
		campo4.setAtributoId(att2.getId());
		campo4.setDatos("datos4");
		linea2 = lineaFactory.create();
		linea2o = new LineaOperations(linea2);
		linea2.setNombre("nombre linea 2");
		linea2.setPropuestaId(propuesta.getId());
		linea2o.addCampo(campo3);
		linea2o.addCampo(campo4);
		propuesta.operations().addLineaId(linea1.getId());
		propuesta.operations().addAttribute(att1);
		propuesta.operations().addAttribute(att2);
		lineas = new ArrayList<>();
		lineas.add(linea1);
		lineas.add(linea2);
		
		
		AtributoForLineaFormDto a1 = modelMapper.map(att1, AtributoForLineaFormDto.class);
		a1.setValue("valor1");
		AtributoForLineaFormDto a2 = modelMapper.map(att2, AtributoForLineaFormDto.class);
		a2.setValue("valor2");
		List<AtributoForLineaFormDto> as = new ArrayList<>();
		as.add(a1);
		as.add(a2);
		lineaWithAttListDto = new LineaWithAttListDto(linea1, as, 1);
		
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(consultaService.findPropuestaByPropuestaId(propuesta.getId())).thenReturn(Mono.just(propuesta));
	}
	
	@Test
	void testGetAttributesOfProposal() {
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(att1,att2));
		Mono<LineaWithAttListDto> mono = lineaUtilities.getAttributesOfProposal(linea1, propuesta.getId(), 1);
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
		LineaWithAttListDto dto = new LineaWithAttListDto(linea1, as, 1);
		Flux<Boolean> assertions = lineaUtilities.assertBindingResultOfListDto(dto, bindingResult, "attributes");
		
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
		LineaWithAttListDto dto = new LineaWithAttListDto(linea1, as, 1);
		
		Mono<Linea> li = lineaUtilities.reconstructLine(dto);
		StepVerifier.create(li)
		.assertNext(rL -> {
			LineaOperations rLo = new LineaOperations(rL);
			assertEquals(linea1.getNombre(), rL.getNombre());
			assertEquals(2, rLo.getCantidadCampos());
			assertEquals(att1.getId(), rLo.getCampoByIndex(0).getAtributoId());
			assertEquals(a2.getValue(), rLo.getCampoByIndex(1).getDatosText());
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
			LineaOperations m0 = new LineaOperations(m.get(0));
			LineaOperations m1 = new LineaOperations(m.get(1));
			assertEquals(att1.getId(), m0.getCampoByIndex(0).getAtributoId());
			assertEquals(campo1.getDatosText(), m0.getCampoByIndex(0).getDatosText());
			assertEquals(att2.getId(), m0.getCampoByIndex(1).getAtributoId());
			assertEquals(campo2.getDatosText(), m0.getCampoByIndex(1).getDatosText());
			assertEquals(campo1.getDatosText(), m0.getLinea().getNombre());
			assertEquals(att1.getId(), m1.getCampoByIndex(0).getAtributoId());
			assertEquals(campo3.getDatosText(), m1.getCampoByIndex(0).getDatosText());
			assertEquals(att2.getId(), m1.getCampoByIndex(1).getAtributoId());
			assertEquals(campo4.getDatosText(), m1.getCampoByIndex(1).getDatosText());
			assertEquals(campo3.getDatosText(), m.get(1).getNombre());
		})
		.expectComplete()
		.verify()
		;
	}
	
	@Test
	void testAssertNameBindingResultOfListDto() {
		lineaWithAttListDto.getLinea().setNombre("");
		BindException bindingResult = new BindException(lineaWithAttListDto, "lineaWithAttListDto");
		lineaUtilities.assertNameBindingResultOfListDto(lineaWithAttListDto, bindingResult, "linea.nombre");
		assertTrue(bindingResult.hasErrors());
	}
	
	@Test
	void testSetringListOfListsFromPropuestaAndLineas() {
		Mono<StringListOfListsWrapper> wrap = lineaUtilities.stringListOfListsFromPropuestaId(propuesta.getId());
		
		StepVerifier.create(wrap)
			.assertNext(w -> {
				assertEquals(2, w.getName().size());
				assertNull(w.getName().get(0));
				assertNull(w.getName().get(1));
				assertEquals(2, w.getStrings().size());
				assertEquals(att1.getName(), w.getStrings().get(0));
				assertEquals(att2.getName(), w.getStrings().get(1));
				assertEquals(2, w.getStringListWrapper().size());
				assertEquals(linea1.getNombre(), w.getStringListWrapper().get(0).getName());
				assertEquals(linea2.getNombre(), w.getStringListWrapper().get(1).getName());
				assertEquals(campo1.getDatosText(), w.getStringListWrapper().get(0).getString().get(0));
				assertEquals(campo2.getDatosText(), w.getStringListWrapper().get(0).getString().get(1));
				assertEquals(campo3.getDatosText(), w.getStringListWrapper().get(1).getString().get(0));
				assertEquals(campo4.getDatosText(), w.getStringListWrapper().get(1).getString().get(1));
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	void test_get_ProposalId_VS_SetOfCounterLineId() {
		var pp1 = new PropuestaProveedor();
		pp1.setForProposalId(propuesta.getId());
		var pp2 = new PropuestaProveedor();
		pp2.setForProposalId(propuesta.getId());
		
		var lp1a = lineaFactory.create();
		lp1a.setPropuestaId(pp1.getId());
		lp1a.setCounterLineId(new ArrayList<String>(Arrays.asList("counter1a")));
		var lp1b = lineaFactory.create();
		lp1b.setPropuestaId(pp1.getId());
		lp1b.setCounterLineId(new ArrayList<String>(Arrays.asList("counter1b")));
		
		var lp2a = lineaFactory.create();
		lp2a.setPropuestaId(pp2.getId());
		lp2a.setCounterLineId(new ArrayList<String>(Arrays.asList("counter2a")));
		var lp2b = lineaFactory.create();
		lp2b.setPropuestaId(pp2.getId());
		lp2b.setCounterLineId(new ArrayList<String>(Arrays.asList("counter2b")));
		
		when(compoundedService.getAllLineasOfPropuestasAssignedTo(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(lp1a, lp1b, lp2a, lp2b));
		//when(consultaService.getAllPropuestaProveedorAsignedTo(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(pp1, pp2));
		
		Map<String, Set<String>> map = lineaUtilities.get_ProposalId_VS_SetOfCounterLineId(propuesta.getId()).block();
		
		assertEquals(2, map.keySet().size());
		assertTrue(map.keySet().contains(pp1.getId()));
		assertTrue(map.keySet().contains(pp2.getId()));
		assertTrue(map.get(pp1.getId()).contains("counter1a"));
		assertTrue(map.get(pp1.getId()).contains("counter1b"));
		assertTrue(map.get(pp2.getId()).contains("counter2a"));
		assertTrue(map.get(pp2.getId()).contains("counter2b"));
	}

}
