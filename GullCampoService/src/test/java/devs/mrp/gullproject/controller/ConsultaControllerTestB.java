package devs.mrp.gullproject.controller;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.gullproject.configuration.MapperConfig;
import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.CosteLineaProveedor;
import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import devs.mrp.gullproject.domains.Pvper;
import devs.mrp.gullproject.domains.PvperLinea;
import devs.mrp.gullproject.domains.PvperSum;
import devs.mrp.gullproject.domains.TipoPropuesta;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.CostesCheckboxWrapper;
import devs.mrp.gullproject.domains.dto.PvpsCheckboxWrapper;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.AtributoUtilities;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import devs.mrp.gullproject.service.PropuestaUtilities;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ConsultaController.class)
@AutoConfigureWebTestClient
@Import({MapperConfig.class, PropuestaUtilities.class, AtributoUtilities.class})
@ActiveProfiles("default")
class ConsultaControllerTestB {
	
	WebTestClient webTestClient;
	ConsultaController consultaController;
	ModelMapper modelMapper;
	
	@MockBean
	ConsultaService consultaService;
	@MockBean
	LineaService lineaService;
	@MockBean
	AtributoServiceProxyWebClient atributoService;
	
	@Autowired
	public ConsultaControllerTestB(WebTestClient webTestClient, ConsultaController consultaController, ModelMapper modelMapper) {
		this.webTestClient = webTestClient;
		this.consultaController = consultaController;
		this.modelMapper = modelMapper;
	}
	
	Propuesta prop1;
	Propuesta prop2;
	Propuesta prop3;
	Propuesta propuestaProveedor;
	Propuesta propuestaNuestra;
	Consulta consulta1;
	Consulta consulta2;
	Mono<Consulta> mono1;
	Mono<Consulta> mono2;
	
	Linea linea1;
	Linea linea2;
	Linea linea3;
	Linea linea4;
	
	AtributoForCampo att1;
	AtributoForCampo att2;
	AtributoForCampo att3;
	
	@BeforeEach
	void init() {
		
		att1 = new AtributoForCampo();
		att1.setId("idAtt1");
		att1.setName("nameAtt1");
		att1.setTipo("tipoAtt1");
		
		att2 = new AtributoForCampo();
		att2.setId("idAtt2");
		att2.setName("nameAtt2");
		att2.setTipo("tipoAtt2");
		
		att3 = new AtributoForCampo();
		att3.setId("idAtt3");
		att3.setName("nameAtt3");
		att3.setTipo("tipoAtt3");
		
		linea1 = new Linea();
		linea1.setNombre("l1");
		linea2 = new Linea();
		linea2.setNombre("l2");
		linea3 = new Linea();
		linea3.setNombre("l3");
		linea4 = new Linea();
		linea4.setNombre("l4");
		
		prop1 = new PropuestaCliente() {};
		var op1 = prop1.operations();
		op1.addLineaId("linea1");
		op1.addLineaId("linea2");
		op1.addAttribute(att1);
		op1.addAttribute(att2);
		prop1.setNombre("propuesta 1");
		
		prop2 = new PropuestaCliente() {};
		var op2 = prop2.operations();
		op2.addLineaId("linea3");
		prop2.setNombre("propuesta 2");
		
		consulta1 = new Consulta();
		consulta1.setNombre("consulta 1");
		consulta1.setStatus("estado 1");
		consulta1.setId("idConsulta1");
		consulta1.operations().addPropuesta(prop1);
		consulta1.operations().addPropuesta(prop2);
		
		prop3 = new PropuestaCliente() {};
		var op3 = prop3.operations();
		op3.addLineaId(linea4.getId());
		prop3.setNombre("propuesta 3");
		
		consulta2 = new Consulta();
		consulta2.setNombre("consulta 2");
		consulta2.setStatus("estado 2");
		consulta2.setId("idConsulta2");
		consulta2.operations().addPropuesta(prop3);
		
		mono1 = Mono.just(consulta1);
		mono2 = Mono.just(consulta2);
		
		propuestaProveedor = new PropuestaProveedor(prop1.getId());
		propuestaProveedor.setAttributeColumns(prop1.getAttributeColumns());
		propuestaProveedor.setLineaIds(prop1.getLineaIds());
		propuestaProveedor.setNombre("propuesta proveedor name");
		List<CosteProveedor> costes = new ArrayList<>();
		CosteProveedor cos1 = new CosteProveedor();
		cos1.setName("COSTE BASE");
		costes.add(cos1);
		((PropuestaProveedor)propuestaProveedor).setCostes(costes);
		
		propuestaNuestra = new PropuestaNuestra(prop1.getId());
		propuestaNuestra.setAttributeColumns(prop1.getAttributeColumns());
		propuestaNuestra.setLineaIds(prop1.getLineaIds());
		propuestaNuestra.setNombre("propuesta nuestra name");
		List<Pvper> pvpers = new ArrayList<>();
		Pvper pvp1 = new Pvper();
		pvp1.setIdCostes(new ArrayList<String>() {{add(cos1.getId());}});
		pvp1.setName("pvp1 name");
		pvpers.add(pvp1);
		((PropuestaNuestra)propuestaNuestra).setPvps(pvpers);
		List<PvperSum> sums = new ArrayList<>();
		PvperSum sum1 = new PvperSum();
		sum1.setPvperIds(new ArrayList<String>() {{add(pvp1.getId());}});
		sum1.setName("sum1 name");
		sums.add(sum1);
		((PropuestaNuestra)propuestaNuestra).setSums(sums);
		
		
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(prop1.getId()))).thenReturn(Mono.just(prop1));
		when(consultaService.findAttributesByPropuestaId(prop1.getId())).thenReturn(Flux.fromIterable(consulta1.operations().getPropuestaById(prop1.getId()).getAttributeColumns()));
		when(atributoService.getAllAtributos()).thenReturn(Flux.just(att1, att2, att3));
		
		when(consultaService.findCostesByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.just(cos1));
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Mono.just(consulta1));
		when(consultaService.updateCostesOfPropuesta(ArgumentMatchers.eq(propuestaProveedor.getId()), ArgumentMatchers.anyList())).thenReturn(Mono.just(consulta1));
		when(consultaService.addCostToList(ArgumentMatchers.eq(propuestaProveedor.getId()), ArgumentMatchers.any(CosteProveedor.class))).thenReturn(Mono.just(consulta1));
		when(consultaService.keepUnselectedCosts(ArgumentMatchers.eq(propuestaProveedor.getId()), ArgumentMatchers.any(CostesCheckboxWrapper.class))).thenReturn(Mono.just(consulta1));
		
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuestaNuestra.getId()))).thenReturn(Mono.just(consulta1));
		when(consultaService.addPvpToList(ArgumentMatchers.eq(propuestaNuestra.getId()), ArgumentMatchers.any(Pvper.class))).thenReturn(Mono.just(consulta1));
		when(consultaService.keepUnselectedPvps(ArgumentMatchers.eq(propuestaNuestra.getId()), ArgumentMatchers.any(PvpsCheckboxWrapper.class))).thenReturn(Mono.just(consulta1));
		when(consultaService.updatePvpsOfPropuesta(ArgumentMatchers.eq(propuestaNuestra.getId()), ArgumentMatchers.anyList())).thenReturn(Mono.just(consulta1));
		
		when(consultaService.addPvpSumToList(ArgumentMatchers.eq(propuestaNuestra.getId()), ArgumentMatchers.any(PvperSum.class))).thenReturn(Mono.just(consulta1));
	}
	
	private void addCosts() {
		consulta1.getPropuestas().add(propuestaProveedor);
		CosteLineaProveedor cost = new CosteLineaProveedor();
		cost.setValue(123.45);
		cost.setCosteProveedorId(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId());
		List<CosteLineaProveedor> costs = new ArrayList<>();
		costs.add(cost);
		linea1.setCostesProveedor(costs);
		linea2.setCostesProveedor(costs);
		
		consulta1.getPropuestas().add(propuestaNuestra);
		PvperLinea pvp = new PvperLinea();
		pvp.setMargen(28.5);
		pvp.setPvp(126.53);
		pvp.setPvperId(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId());
		List<PvperLinea> pvps = new ArrayList<>();
		pvps.add(pvp);
		linea1.setPvps(pvps);
		linea2.setPvps(pvps);
		
		List<String> sums = new ArrayList<>();
		sums.add(((PropuestaNuestra)propuestaNuestra).getSums().get(0).getId());
		linea1.setPvpSums(sums);
		linea2.setPvpSums(sums);
	}

	@Test
	void testCrearConsulta() throws Exception {
		
		webTestClient.get()
			.uri("/consultas/nuevo")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nombre:")
						.contains("Estado:")
						.contains("Nueva Consulta")
						.doesNotContain("Guardada. ¿Añadir otra?");
			});
		
		webTestClient.get()
			.uri("/consultas/nuevo?add=1")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Estado:")
					.contains("Nueva Consulta")
					.contains("Guardada. ¿Añadir otra?");
		});
	}
	
	@Test
	void testProcessNewConsulta() {
		Consulta a = new Consulta();
		a.setNombre("name of consulta");
		a.setStatus("open status");
		a.setId("idConsulta");
		
		Consulta b = new Consulta();
		b.setNombre("name of consulta");
		b.setStatus("open status");
		
		Mono<Consulta> mono1 = Mono.just(a);
		when(consultaService.save(ArgumentMatchers.refEq(b, "createdTime", "id"))).thenReturn(mono1); // excludes "createdTime" from the match
		
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
	    formData.add("nombre", "name of consulta");
	    formData.add("status", "open status");
		
		webTestClient.post()
		.uri("/consultas/nuevo")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData(formData))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Estado:")
					.contains("Consulta Guardada")
					.doesNotContain("errores")
					.contains("name of consulta")
					.contains("open status")
					.contains("Consulta Guardada Como...");
		});
		
		webTestClient.post()
		.uri("/consultas/nuevo")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("nombre", "")
				.with("status", "cancelled"))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Estado:")
					.contains("Nueva Consulta")
					.contains("errores")
					.contains("El nombre es obligatorio")
					.doesNotContain("Guardada. ¿Añadir otra?")
					.doesNotContain("Consulta Guardada")
					.contains("cancelled");
		});
		
		webTestClient.post()
		.uri("/consultas/nuevo")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("nombre", "este es incorrecto")
				.with("status", ""))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Estado:")
					.contains("Nueva Consulta")
					.contains("errores")
					.contains("El estado es obligatorio.")
					.doesNotContain("Guardada. ¿Añadir otra?")
					.doesNotContain("Consulta Guardada")
					.contains("este es incorrecto");
		});
	}
	
	@Test
	void testShowAllConsultas() throws Exception {
		
		Consulta a = new Consulta();
		a.setNombre("consulta 1");
		a.setStatus("estado 1");
		a.setId("idConsulta1");
		
		Consulta b = new Consulta();
		b.setNombre("consulta 2");
		b.setStatus("estado 2");
		b.setId("idConsulta2");
		
		Flux<Consulta> flux = Flux.just(a, b);
		when(consultaService.findAll()).thenReturn(flux);
		
		webTestClient.get()
			.uri("/consultas/all")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Todas Las Consultas")
						.contains(a.getNombre())
						.contains(b.getNombre())
						.contains("Crear nueva consulta");
			});
	}
	
	@Test
	void testReviewConsultaById() throws Exception {
		
		Propuesta prop1 = new PropuestaCliente() {};
		var op1 = prop1.operations();
		op1.addLineaId("linea1");
		op1.addLineaId("linea2");
		prop1.setNombre("propuesta 1");
		Propuesta prop2 = new PropuestaCliente() {};
		var op2 = prop2.operations();
		op2.addLineaId("linea3");
		prop2.setNombre("propuesta 2");
		Propuesta prop3 = new PropuestaProveedor(prop1.getId());
		prop3.setNombre("propuesta 3");
		Propuesta prop4 = new PropuestaProveedor(prop2.getId());
		prop4.setNombre("propuesta 4");
		Propuesta prop5 = new PropuestaNuestra(prop1.getId());
		prop5.setNombre("propuesta 5");
		Propuesta prop6 = new PropuestaNuestra(prop2.getId());
		prop6.setNombre("propuesta 6");
		
		List<CosteProveedor> costes = new ArrayList<>();
		CosteProveedor cos1 = new CosteProveedor();
		cos1.setName("COSTE BASE");
		costes.add(cos1);
		((PropuestaProveedor)prop3).setCostes(costes);
		((PropuestaProveedor)prop4).setCostes(costes);
		
		List<Pvper> pvpers = new ArrayList<>();
		Pvper pvp1 = new Pvper();
		pvp1.setIdCostes(new ArrayList<String>() {{add(cos1.getId());}});
		pvp1.setName("PVP BASE");
		pvpers.add(pvp1);
		((PropuestaNuestra)prop5).setPvps(pvpers);
		((PropuestaNuestra)prop6).setPvps(pvpers);
		List<PvperSum> sums = new ArrayList<>();
		PvperSum sum1 = new PvperSum();
		sum1.setName("SUM BASE");
		sum1.setPvperIds(new ArrayList<String>() {{add(pvp1.getId());}});
		sums.add(sum1);
		((PropuestaNuestra)prop5).setSums(sums);
		((PropuestaNuestra)prop6).setSums(sums);
		
		Consulta a = new Consulta();
		a.setNombre("consulta 1");
		a.setStatus("estado 1");
		a.setId("idConsulta1");
		a.operations().addPropuesta(prop1);
		a.operations().addPropuesta(prop2);
		a.operations().addPropuesta(prop3);
		a.operations().addPropuesta(prop5);
		
		Consulta b = new Consulta();
		b.setNombre("consulta 2");
		b.setStatus("estado 2");
		b.setId("idConsulta2");
		
		Mono<Consulta> mono1 = Mono.just(a);
		Mono<Consulta> mono2 = Mono.just(b);
		when(consultaService.findById(ArgumentMatchers.eq(a.getId()))).thenReturn(mono1);
		when(consultaService.findById(ArgumentMatchers.eq(b.getId()))).thenReturn(mono2);
		when(consultaService.findAllPropuestasOfConsulta(ArgumentMatchers.eq(a.getId()))).thenReturn(Flux.fromIterable(a.getPropuestas()));
		when(consultaService.findAllPropuestasOfConsulta(ArgumentMatchers.eq(b.getId()))).thenReturn(Flux.just());
		
		webTestClient.get()
			.uri("/consultas/revisar/id/idConsulta1")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nombre")
						.contains("Estado")
						.contains("Creado")
						.contains("Propuesta de Cliente")
						.contains("Lineas")
						.contains("Atributos")
						.contains("consulta 1")
						.contains("estado 1")
						.contains("propuesta 1")
						.contains("propuesta 2")
						.contains("Respuestas de Proveedores")
						.contains(prop3.getNombre())
						.doesNotContain(prop4.getNombre())
						.contains("Ofertas Nuestras")
						.contains(prop5.getNombre())
						.doesNotContain(prop6.getNombre())
						.contains("Añadir una solicitud o revisión del cliente");
			});
		
		webTestClient.get()
		.uri("/consultas/revisar/id/idConsulta2")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre")
					.contains("Estado")
					.contains("Creado")
					.doesNotContain("Propuesta de Cliente")
					.doesNotContain("Lineas")
					.contains("consulta 2")
					.contains("estado 2")
					.doesNotContain("propuesta 1")
					.doesNotContain("propuesta 2")
					.doesNotContain("Respuestas de Proveedores")
					.contains("Añadir una solicitud o revisión del cliente");
		});
	}
	
	@Test
	void testAddPropuestaToId() throws Exception {
		
		webTestClient.get()
			.uri("/consultas/revisar/id/idConsulta1/addpropuesta")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nueva Solicitud de Propuesta")
						.contains("Nombre")
						.contains("Ok")
						.contains("Volver")
						.contains(att1.getName())
						.contains(att2.getName())
						.contains(att3.getName());
			});
	}
	
	@Test
	void testProcessNewPropuesta() {
		
		consulta2.operations().addPropuesta(prop1);
		when(consultaService.addPropuesta(ArgumentMatchers.eq(consulta2.getId()), ArgumentMatchers.any(Propuesta.class))).thenReturn(Mono.just(consulta2));
		
		webTestClient.post()
		.uri("/consultas/revisar/id/idConsulta2")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("propuestaCliente.nombre", prop1.getNombre())
				.with("propuestaCliente.tipoPropuesta", TipoPropuesta.CLIENTE.toString())
				.with("propuestaCliente.forProposalId", consulta1.getId())
				
				.with("attributes[0].selected", "true")
				.with("attributes[0].localIdentifier", att1.getLocalIdentifier())
				.with("attributes[0].id", att1.getId())
				.with("attributes[0].name", att1.getName())
				.with("attributes[0].tipo", att1.getTipo())
				
				.with("attributes[1].selected", "false")
				.with("attributes[1].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[1].id", att2.getId())
				.with("attributes[1].name", att2.getName())
				.with("attributes[1].tipo", att2.getTipo())
				
				.with("attributes[2].selected", "false")
				.with("attributes[2].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[2].id", att2.getId())
				.with("attributes[2].name", att2.getName())
				.with("attributes[2].tipo", att2.getTipo())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Propuesta Guardada")
					.contains("Guardando...")
					.contains("Nombre:")
					.doesNotContain("errores")
					.contains(prop1.getNombre())
					.contains("Volver a la consulta");
		});
		
		prop1.setNombre("");
		consulta2.operations().addPropuesta(prop1);
		
		webTestClient.post()
		.uri("/consultas/revisar/id/idConsulta2")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("propuestaCliente.nombre", "")
				.with("propuestaCliente.tipoPropuesta", TipoPropuesta.CLIENTE.toString())
				
				.with("attributes[0].selected", "true")
				.with("attributes[0].localIdentifier", att1.getLocalIdentifier())
				.with("attributes[0].id", att1.getId())
				.with("attributes[0].name", att1.getName())
				.with("attributes[0].tipo", att1.getTipo())
				
				.with("attributes[1].selected", "false")
				.with("attributes[1].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[1].id", att2.getId())
				.with("attributes[1].name", att2.getName())
				.with("attributes[1].tipo", att2.getTipo())
				
				.with("attributes[2].selected", "false")
				.with("attributes[2].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[2].id", att2.getId())
				.with("attributes[2].name", att2.getName())
				.with("attributes[2].tipo", att2.getTipo())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nueva Solicitud de Propuesta")
					.contains("Corrige los errores y reenvía.")
					.contains("Selecciona un nombre")
					.contains("Nombre:")
					.contains("Volver");
		});
	}
	
	@Test
	void testDeleteConsultaById() throws Exception {
		
		when(consultaService.findById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(mono1);
		
		webTestClient.get()
			.uri("/consultas/delete/id/idConsulta1")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Borrar Consulta")
						.contains("¿Seguro que deseas borrar esta consulta?")
						.contains("Nombre:")
						.contains("Estado:")
						.contains(consulta1.getNombre())
						.contains(consulta1.getStatus())
						.contains("Volver");
			});
	}
	
	@Test
	void testProcessDeleteConsultaById() {
		
		when(consultaService.deleteById(ArgumentMatchers.anyString())).thenReturn(Mono.just(0L));
		when(consultaService.deleteById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(Mono.just(1L)); // latest rules
		when(consultaService.findById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(Mono.just(consulta1));
		when(consultaService.findById(ArgumentMatchers.eq(consulta2.getId()))).thenReturn(Mono.just(consulta2));
		
		when(lineaService.deleteSeveralLineasFromSeveralPropuestas(ArgumentMatchers.eq(consulta1.getPropuestas()))).thenReturn(Mono.just(3L));
		when(lineaService.deleteSeveralLineasFromSeveralPropuestas(ArgumentMatchers.eq(consulta2.getPropuestas()))).thenReturn(Mono.just(1L));
		
		webTestClient.post()
		.uri("/consultas/delete/id/idConsulta1")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("id", consulta1.getId())
				.with("nombre", consulta1.getNombre())
				.with("status", consulta1.getStatus()))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Borrar Consulta")
					.contains("Consulta borrada correctamente")
					.contains("2 propuestas borradas")
					.contains("3 lineas borradas")
					.doesNotContain("Algo no ha ido correctamente")
					.contains("Volver");
		});
		
		webTestClient.post()
		.uri("/consultas/delete/id/idConsulta2")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("id", consulta2.getId())
				.with("nombre", consulta2.getNombre())
				.with("status", consulta2.getStatus()))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Borrar Consulta")
					.doesNotContain("Consulta borrada correctamente")
					.contains("Algo no ha ido correctamente")
					.contains("Volver");
		});
		
	}
	
	@Test
	void testDeletePropuestaById() throws Exception {
		
		when(consultaService.findById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(mono1);
		
		webTestClient.get()
			.uri("/consultas/delete/id/"+consulta1.getId()+"/propuesta/"+prop1.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Borrar Propuesta")
						.contains("¿Seguro que deseas borrar esta propuesta?")
						.contains("Nombre:")
						.contains(prop1.getNombre())
						.contains("Volver");
			});
	}
	
	@Test
	void testProcessDeletePropuestaById() throws Exception {
		
		when(consultaService.findById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(mono1);
		Consulta consulta3 = consulta1;
		consulta3.operations().removePropuesta(prop1);
		when(consultaService.removePropuesta(consulta1.getId(), prop1)).thenReturn(Mono.just(consulta3));
		when(consultaService.removePropuestaById(consulta1.getId(), prop1.getId())).thenReturn(Mono.just(consulta3.operations().getCantidadPropuestas()));
		
		when(lineaService.deleteSeveralLineasFromPropuestaId(ArgumentMatchers.eq(prop1.getId()))).thenReturn(Mono.just(2L));
		
		webTestClient.post()
			.uri("/consultas/delete/id/"+consulta1.getId()+"/propuesta/"+prop1.getId())
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("idConsulta", consulta1.getId())
					.with("idPropuesta", prop1.getId()))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Borrar Propuesta")
						.contains("1 propuesta borrada")
						.contains("2 lineas borradas")
						.contains("Volver");
			});
	}
	
	@Test
	void testShowAttributesOfProposal() {
		
		when(consultaService.findConsultaByPropuestaId(prop1.getId())).thenReturn(Mono.just(consulta1));
		when(consultaService.findAttributesByPropuestaId(prop1.getId())).thenReturn(Flux.fromIterable(consulta1.operations().getPropuestaById(prop1.getId()).getAttributeColumns()));
		
		webTestClient.get()
		.uri("/consultas/attof/propid/"+consulta1.operations().getPropuestaById(prop1.getId()).getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Atributos de la propuesta")
					.contains("Modificar atributos")
					.contains("Nombre")
					.contains("Tipo")
					.contains(prop1.getNombre())
					.contains(prop1.getAttributeColumns().get(0).getName())
					.contains(prop1.getAttributeColumns().get(0).getTipo())
					.contains(prop1.getAttributeColumns().get(1).getName())
					.contains(prop1.getAttributeColumns().get(1).getTipo())
					.contains("Volver");
		});
		
	}
	
	@Test
	void testAddAttributeToProposal() {
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(prop1.getId()))).thenReturn(Mono.just(prop1));
		ArrayList<AtributoForCampo> atts = new ArrayList<>();
		atts.add(att1);
		atts.add(att2);
		atts.add(att3);
		when(atributoService.getAllAtributos()).thenReturn(Flux.fromIterable(atts));
		
		webTestClient.get()
			.uri("/consultas/attof/propid/" + prop1.getId() + "/new")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Seleccionar Atributos")
					.contains("Selecciona los atributos que corresponden a esta propuesta:")
					.contains(att1.getName())
					.contains(att2.getName())
					.contains(att3.getName());
			});
	}
	
	@Test
	void testProcessAddAttributeToProposal() {
		ArrayList<AtributoForFormDto> listDtos = new ArrayList<>();
		ArrayList<AtributoForCampo> list = new ArrayList<>();
		AtributoForFormDto attdto1 = modelMapper.map(att1, AtributoForFormDto.class);
		attdto1.setSelected(false);
		AtributoForFormDto attdto2 = modelMapper.map(att2, AtributoForFormDto.class);
		attdto2.setSelected(true);
		AtributoForFormDto attdto3 = modelMapper.map(att3, AtributoForFormDto.class);
		attdto3.setSelected(true);
		listDtos.add(attdto1);
		listDtos.add(attdto2);
		listDtos.add(attdto3);
		//list.add(att1); // this one is filtered out by selected=false
		list.add(att2);
		list.add(att3);
		
		consulta1.operations().getPropuestaById(prop1.getId()).setAttributeColumns(list);
		
		when(consultaService.updateAttributesOfPropuesta(ArgumentMatchers.eq(prop1.getId()), ArgumentMatchers.eq(list))).thenReturn(Mono.just(consulta1));
		
		webTestClient.post()
			.uri("/consultas/attof/propid/" + prop1.getId() + "/new")
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("attributes[0].selected", "false") // att1
										.with("attributes[0].localIdentifier", att1.getLocalIdentifier())
										.with("attributes[0].id", att1.getId())
										.with("attributes[0].name", att1.getName())
										.with("attributes[0].tipo", att1.getTipo())
										.with("attributes[1].selected", "true") // att2
										.with("attributes[1].localIdentifier", att2.getLocalIdentifier())
										.with("attributes[1].id", att2.getId())
										.with("attributes[1].name", att2.getName())
										.with("attributes[1].tipo", att2.getTipo())
										.with("attributes[2].selected", "true") // att3
										.with("attributes[2].localIdentifier", att3.getLocalIdentifier())
										.with("attributes[2].id", att3.getId())
										.with("attributes[2].name", att3.getName())
										.with("attributes[2].tipo", att3.getTipo()))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.doesNotContain(att1.getName())	
					.contains(att2.getName())
					.contains(att3.getName())
					.contains("Atributo Guardado")
					.contains("Los siguientes atributos...")
					.contains("Constituyen ahora la propuesta:")
					.contains(prop1.getNombre());
			});
	}
	
	@Test
	void testEditConsultaDetails() {
		when(consultaService.findById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(Mono.just(consulta1));
		webTestClient.get()
			.uri("/consultas/revisar/id/" + consulta1.getId() + "/edit")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Revisar Consulta")
					.contains(consulta1.getNombre())
					.contains(consulta1.getStatus());
			});
	}
	
	@Test
	void testProcessEditConsultaDetails() {
		String newname = "nuevo nombre";
		String newstatus = "nuevo status";
		consulta2.setNombre(newname);
		consulta2.setStatus(newstatus);
		consulta2.setId(consulta1.getId());
		when(consultaService.updateNameAndStatus(consulta1.getId(), newname, newstatus)).thenReturn(Mono.just(consulta2));
		
		// should update correctly
		webTestClient.post()
			.uri("/consultas/revisar/id/" + consulta1 + "/edit")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("id", consulta1.getId())
					.with("nombre", newname)
					.with("status", newstatus)
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Datos actualizados");
			});
		
		// should show error invalid data entered
		webTestClient.post()
		.uri("/consultas/revisar/id/" + consulta1 + "/edit")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("id", consulta1.getId())
				.with("nombre", "")
				.with("status", newstatus)
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
				.contains("Corrige los errores y reenvía");
		});
	}
	
	@Test
	void testEditarProposalCliente() {
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(prop1.getId()))).thenReturn(Mono.just(prop1));
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(prop1.getId()))).thenReturn(Mono.just(consulta1));
		
		webTestClient.get()
			.uri("/consultas/editar/propcli/" + prop1.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre")
					.contains(prop1.getNombre());
			});
	}
	
	@Test
	void testProcessEditarProposalCliente() {
		String newname = "nuevo nombre";
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.refEq(prop1.getId()))).thenReturn(Mono.just(consulta1));
		when(consultaService.updateNombrePropuesta(ArgumentMatchers.any(Propuesta.class))).thenReturn(Mono.just(consulta1));
		
		// should update correctly
		webTestClient.post()
			.uri("/consultas/editar/propcli/" + prop1.getId())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("nombre", newname)
				.with("id", prop1.getId())
				.with("forProposalId", prop2.getId()))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Datos actualizados");
			});
		
		// should give an error
				webTestClient.post()
					.uri("/consultas/editar/propcli/" + prop1.getId())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.TEXT_HTML)
					.body(BodyInserters.fromFormData("nombre", "")
						.with("id", prop1.getId()))
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.consumeWith(response -> {
						Assertions.assertThat(response.getResponseBody()).asString()
							.contains("Corrige los errores y reenvía")
							.contains("Selecciona un nombre");
					});
	}
	
	@Test
	void testOrderAttributesOfProposal() {
		webTestClient.get()
			.uri("/consultas/attof/propid/" + prop1.getId() + "/order")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Ordenar")
					.contains(prop1.getNombre())
					.contains(prop1.getAttributeColumns().get(0).getName())
					.contains(prop1.getAttributeColumns().get(1).getName());
			});
			;
	}
	
	@Test
	void testProcessOrderAttributesOfProposal() {
		when(consultaService.reOrderAttributesOfPropuesta(ArgumentMatchers.eq(prop1.getId()), ArgumentMatchers.anyList())).thenReturn(Mono.just(prop1));
		
		log.debug("should be ok");
		webTestClient.post()
			.uri("/consultas/attof/propid/" + prop1.getId() + "/order")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("atributos[0].localIdentifier", prop1.getAttributeColumns().get(0).getLocalIdentifier())
				.with("atributos[0].order", String.valueOf(1))
				.with("atributos[1].localIdentifier", prop1.getAttributeColumns().get(1).getLocalIdentifier())
				.with("atributes[1].order", String.valueOf(0))
				)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Orden guardado")
					.doesNotContain("Error");
			})
			;
		
		log.debug("should be error");
		webTestClient.post()
			.uri("/consultas/attof/propid/" + prop1.getId() + "/order")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("atributos[0].localIdentifier", "invalidId")
				.with("atributos[0].order", String.valueOf(1))
				.with("atributos[1].localIdentifier", prop1.getAttributeColumns().get(1).getLocalIdentifier())
				.with("atributes[1].order", String.valueOf(0))
				)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Error")
					.doesNotContain("Orden guardado");
			})
			;
	}
	
	@Test
	void testAddProposalProveedorToProposalCliente() {
		Propuesta propB = new PropuestaProveedor(prop1.getId());
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propB.getId()))).thenReturn(Mono.just(propB));
		
		webTestClient.get()
		.uri("/consultas/revisar/id/"+consulta1.getId()+"/onprop/"+propB.getId()+"/addcotizacionproveedor")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nueva Propuesta Recibida")
					.contains("Nombre")
					.contains("Ok")
					.contains("Volver")
					.contains("PROVEEDOR")
					.contains(att1.getName())
					.contains(att2.getName())
					.contains(att3.getName());
		});
	}
	
	@Test
	void testProcessAddProposalProveedorToProposalCliente() {
		consulta2.operations().addPropuesta(prop2);
		when(consultaService.addPropuesta(ArgumentMatchers.eq(consulta2.getId()), ArgumentMatchers.any(Propuesta.class))).thenReturn(Mono.just(consulta2));
		
		log.debug("should be ok");
		webTestClient.post()
		.uri("/consultas/revisar/id/"+consulta2.getId()+"/onprop/"+prop1.getId()+"/addcotizacionproveedor")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("propuestaProveedor.nombre", prop1.getNombre())
				.with("propuestaProveedor.tipoPropuesta", TipoPropuesta.PROVEEDOR.toString())
				.with("propuestaProveedor.forProposalId", prop2.getId())
				
				.with("attributes[0].selected", "true")
				.with("attributes[0].localIdentifier", att1.getLocalIdentifier())
				.with("attributes[0].id", att1.getId())
				.with("attributes[0].name", att1.getName())
				.with("attributes[0].tipo", att1.getTipo())
				
				.with("attributes[1].selected", "false")
				.with("attributes[1].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[1].id", att2.getId())
				.with("attributes[1].name", att2.getName())
				.with("attributes[1].tipo", att2.getTipo())
				
				.with("attributes[2].selected", "false")
				.with("attributes[2].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[2].id", att2.getId())
				.with("attributes[2].name", att2.getName())
				.with("attributes[2].tipo", att2.getTipo())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Propuesta Guardada")
					.contains("Guardando...")
					.contains("Nombre:")
					.doesNotContain("errores")
					.contains(prop2.getNombre())
					.contains("Volver a la consulta");
		});
		
		prop1.setNombre("");
		consulta2.operations().addPropuesta(prop1);
		
		log.debug("should have errors");
		webTestClient.post()
		.uri("/consultas/revisar/id/"+consulta2.getId()+"/onprop/"+prop1.getId()+"/addcotizacionproveedor")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("propuestaProveedor.nombre", "")
				.with("propuestaProveedor.tipoPropuesta", TipoPropuesta.PROVEEDOR.toString())
				
				.with("attributes[0].selected", "true")
				.with("attributes[0].localIdentifier", att1.getLocalIdentifier())
				.with("attributes[0].id", att1.getId())
				.with("attributes[0].name", att1.getName())
				.with("attributes[0].tipo", att1.getTipo())
				
				.with("attributes[1].selected", "false")
				.with("attributes[1].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[1].id", att2.getId())
				.with("attributes[1].name", att2.getName())
				.with("attributes[1].tipo", att2.getTipo())
				
				.with("attributes[2].selected", "false")
				.with("attributes[2].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[2].id", att2.getId())
				.with("attributes[2].name", att2.getName())
				.with("attributes[2].tipo", att2.getTipo())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nueva Propuesta Recibida")
					.contains("Corrige los errores y reenvía.")
					.contains("Selecciona un nombre")
					.contains("Nombre:")
					.contains("Volver");
		});
	}
	
	/**
	 * ****************************
	 * COSTS
	 * ****************************
	 */
	
	@Test
	void testShowCostsOfProposal() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/costof/propid/" + propuestaProveedor.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Costes de la propuesta")
						.contains("Nombre")
						.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName());
			});
	}
	
	@Test
	void testEditCostsOfProposal() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/edit")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Costes de la propuesta")
						.contains("Nombre")
						.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName());
			});
	}
	
	@Test
	void testProcessEditCostsOfProposal() {
		addCosts();
		
		webTestClient.post()
		.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/edit")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("costes[0].id", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId())
				.with("costes[0].name", "nombre actualizado"))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Costes de la propuesta")
					.contains("Nombre")
					.contains("Guardado")
					.contains("nombre actualizado");
		});
		
		webTestClient.post()
		.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/edit")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("costes[0].id", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId())
				.with("costes[0].name", ""))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Costes de la propuesta")
					.contains("Nombre")
					.contains("error")
					.doesNotContain(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName());
		});
	}
	
	@Test
	void testNewCostOfProposal() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/new")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nuevo coste de la propuesta")
						.contains("Nombre");
			});
	}
	
	@Test
	void testProcessNewCostOfProposal() {
		addCosts();
		
		webTestClient.post()
			.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/new")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("id", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId())
					.with("name", "nombre nuevo"))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nuevo coste de la propuesta")
						.contains("Con nombre")
						.contains("nombre nuevo");
			})
			;
		
		webTestClient.post()
		.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("id", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId())
				.with("name", ""))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nuevo coste de la propuesta")
					.doesNotContain("Con nombre")
					.contains("El nombre no debe estar vacío")
					.doesNotContain("nombre nuevo");
		})
		;
	}
	
	@Test
	void testDeleteCostsOfProposal() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/delete")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Borrar costes de la propuesta")
						.contains("Nombre")
						.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName());
			})
			;
	}
	
	@Test
	void testConfirmDeleteCostsOfProposal() {
		addCosts();
		webTestClient.post()
			.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/delete")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("costes[0].selected", "true")
					.with("costes[0].id", "idcoste1")
					.with("costes[0].name" , "nombre coste 1")
					
					.with("costes[1].selected" , "false")
					.with("costes[1].id", "idcoste2")
					.with("costes[1].name", "nombre coste 2")
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("idcoste1")
						.contains("nombre coste 2")
						.contains("Borrar costes de la propuesta");
			})
			;
	}
	
	@Test
	void testProcessDeleteCostsOfProposal() {
		addCosts();
		webTestClient.post()
		.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/delete/confirm")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("costes[0].selected", "true")
				.with("costes[0].id", "idcoste1")
				.with("costes[0].name" , "nombre coste 1")
				
				.with("costes[1].selected" , "false")
				.with("costes[1].id", "idcoste2")
				.with("costes[1].name", "nombre coste 2")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
					.contains("Borrados");
		})
		;
	}
	
	@Test
	void testOrderCostsOfProposal() {
		addCosts();
		webTestClient.get()
		.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/order")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Ordenar costes de la propuesta")
					.contains("Nombre")
					.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName());
		});
	}
	
	@Test
	void testProcessOrderCostsOfProposal() {
		addCosts();
		webTestClient.post()
			.uri("/consultas/costof/propid/" + propuestaProveedor.getId() + "/order")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("costes[0].id", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId())
					.with("costes[0].name", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
					.with("costes[0].order" , "2")
					
					.with("costes[1].id", "otroid")
					.with("costes[1].name", "otro name")
					.with("costes[1].order" , "1")
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
						.contains("Ordenar costes de la propuesta");
			})
			;
	}
	
	/**
	 * *****************************
	 * OFERTA NUESTRA
	 * *****************************
	 */
	
	@Test
	void testAddOurOfferToProposalCliente() {
		Propuesta propB = new PropuestaNuestra(prop1.getId());
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propB.getId()))).thenReturn(Mono.just(propB));
		
		webTestClient.get()
		.uri("/consultas/revisar/id/"+consulta1.getId()+"/onprop/"+propB.getId()+"/addofertanuestra")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nueva oferta para el cliente")
					.contains("Nombre")
					.contains("Ok")
					.contains("Volver")
					.contains("NUESTRA")
					.contains(att1.getName())
					.contains(att2.getName())
					.contains(att3.getName());
		});
	}
	
	@Test
	void testProcessAddOurOfferToProposalCliente() {
		consulta2.operations().addPropuesta(prop2);
		when(consultaService.addPropuesta(ArgumentMatchers.eq(consulta2.getId()), ArgumentMatchers.any(Propuesta.class))).thenReturn(Mono.just(consulta2));
		
		log.debug("should be ok");
		webTestClient.post()
		.uri("/consultas/revisar/id/"+consulta2.getId()+"/onprop/"+prop1.getId()+"/addofertanuestra")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("propuestaNuestra.nombre", prop1.getNombre())
				.with("propuestaNuestra.tipoPropuesta", TipoPropuesta.NUESTRA.toString())
				.with("propuestaNuestra.forProposalId", prop2.getId())
				
				.with("attributes[0].selected", "true")
				.with("attributes[0].localIdentifier", att1.getLocalIdentifier())
				.with("attributes[0].id", att1.getId())
				.with("attributes[0].name", att1.getName())
				.with("attributes[0].tipo", att1.getTipo())
				
				.with("attributes[1].selected", "false")
				.with("attributes[1].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[1].id", att2.getId())
				.with("attributes[1].name", att2.getName())
				.with("attributes[1].tipo", att2.getTipo())
				
				.with("attributes[2].selected", "false")
				.with("attributes[2].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[2].id", att2.getId())
				.with("attributes[2].name", att2.getName())
				.with("attributes[2].tipo", att2.getTipo())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Propuesta Guardada")
					.contains("Guardando...")
					.contains("Nombre:")
					.doesNotContain("errores")
					.contains(prop2.getNombre())
					.contains("Volver a la consulta");
		});
		
		prop1.setNombre("");
		consulta2.operations().addPropuesta(prop1);
		
		log.debug("should have errors");
		webTestClient.post()
		.uri("/consultas/revisar/id/"+consulta2.getId()+"/onprop/"+prop1.getId()+"/addofertanuestra")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("propuestaNuestra.nombre", "")
				.with("propuestaNuestra.tipoPropuesta", TipoPropuesta.NUESTRA.toString())
				
				.with("attributes[0].selected", "true")
				.with("attributes[0].localIdentifier", att1.getLocalIdentifier())
				.with("attributes[0].id", att1.getId())
				.with("attributes[0].name", att1.getName())
				.with("attributes[0].tipo", att1.getTipo())
				
				.with("attributes[1].selected", "false")
				.with("attributes[1].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[1].id", att2.getId())
				.with("attributes[1].name", att2.getName())
				.with("attributes[1].tipo", att2.getTipo())
				
				.with("attributes[2].selected", "false")
				.with("attributes[2].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[2].id", att2.getId())
				.with("attributes[2].name", att2.getName())
				.with("attributes[2].tipo", att2.getTipo())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nueva oferta para el cliente")
					.contains("Corrige los errores y reenvía.")
					.contains("Selecciona un nombre")
					.contains("Nombre:")
					.contains("Volver");
		});
	}
	
	/**
	 * **********************
	 * PVPS
	 * **********************
	 */
	
	@Test
	void testShowPvpsOfProposal() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("PVPs de la propuesta")
						.contains("Nombre")
						.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName());
			});
	}
	
	@Test
	void testNewPvpOfPropuesta() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/new")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nuevo pvp de la propuesta")
						.contains("Nombre");
			})
			;
	}
	
	@Test
	void testProcessNewPvpOfPropuesta() {
		addCosts();
		
		log.debug("should be ok");
		webTestClient.post()
			.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/new")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("name", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName())
					.with("id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
					.with("idCostes[0]", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getIdCostes().get(0)))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nuevo pvp de la propuesta")
						.contains("Con nombre")
						.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName());
			})
			;
		
		log.debug("should give name validation error");
		webTestClient.post()
		.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name", "")
				.with("id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
				.with("idCostes[0]", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getIdCostes().get(0)))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nuevo pvp de la propuesta")
					.contains("no debe estar vacío")
					.contains("Error")
					;
		})
		;
		
		log.debug("should give costs error");
		webTestClient.post()
		.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName())
				.with("id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nuevo pvp de la propuesta")
					.contains("Error")
					.contains("Selecciona al menos un coste")
					.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName());
		})
		;
	}
	
	@Test
	void testDeletePvpOfProposal() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/delete")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Borrar pvps de la propuesta")
						.contains("Nombre")
						.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName());
			})
			;
	}
	
	@Test
	void testConfirmDeletePvpsOfProposal() {
		addCosts();
		webTestClient.post()
			.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/delete")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("pvps[0].selected", "true")
					.with("pvps[0].id", "idpvp1")
					.with("pvps[0].name" , "nombre pvp 1")
					.with("pvps[0].idCostes[0]", "coste0pvp1")
					.with("pvps[0].idCostes[1]", "coste1pvp1")
					
					.with("pvps[1].selected" , "false")
					.with("pvps[1].id", "idpvp2")
					.with("pvps[1].name", "nombre pvp 2")
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("idpvp1")
						.contains("nombre pvp 2")
						.contains("coste0pvp1")
						.contains("coste1pvp1")
						.contains("Borrar pvps de la propuesta");
			})
			;
	}
	
	@Test
	void testProcessDeletePvpsOfProposal() {
		addCosts();
		webTestClient.post()
		.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/delete/confirm")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("pvps[0].selected", "true")
				.with("pvps[0].id", "idpvp1")
				.with("pvps[0].name" , "nombre pvp 1")
				.with("pvps[0].idCostes[0]", "coste0pvp1")
				.with("pvps[0].idCostes[1]", "coste1pvp1")
				
				.with("pvps[1].selected" , "false")
				.with("pvps[1].id", "idpvp2")
				.with("pvps[1].name", "nombre pvp 2")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName())
					.contains("Borrados");
		})
		;
	}
	
	@Test
	void testOrderPvpsOfProposal() {
		addCosts();
		webTestClient.get()
		.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/order")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Ordenar pvps de la propuesta")
					.contains("Nombre")
					.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName());
		});
	}
	
	@Test
	void testProcessOrderPvpsOfProposal() {
		addCosts();
		webTestClient.post()
		.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/order")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("pvps[0].id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
				.with("pvps[0].name", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName())
				.with("pvps[0].order" , "2")
				.with("pvps[0].idCostes[0]", "idcoste0")
				.with("pvps[0].idCostes[1]", "idcoste1")
				
				.with("pvps[1].id", "otroid")
				.with("pvps[1].name", "otro name")
				.with("pvps[1].order" , "1")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName())
					.contains("COSTE BASE")
					.contains("Ordenar pvps de la propuesta");
		})
		;
	}
	
	@Test
	void testEditPvpsOfProposal() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/pvpsof/propid/"+propuestaNuestra.getId()+"/edit")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Pvps de la propuesta")
						.contains("Nombre")
						.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName());
			})
			;
	}
	
	@Test
	void testProcessEditPvpsOfProposal() {
		addCosts();
		
		log.debug("should be ok");
		webTestClient.post()
		.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/edit")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("pvps[0].id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
				.with("pvps[0].name", "nombre actualizado")
				.with("pvps[0].costs[0].id", "idpvp1")
				.with("pvps[0].costs[0].selected", "true")
				.with("pvps[0].costs[1].id", "idpvp2")
				.with("pvps[0].costs[1].selected", "false")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Pvps de la propuesta")
					.contains("Nombre")
					.contains("Guardado");
		});
		
		log.debug("should fail on name validation");
		webTestClient.post()
		.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/edit")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("pvps[0].id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
				.with("pvps[0].name", "")
				.with("pvps[0].costs[0].id", "idpvp1")
				.with("pvps[0].costs[0].selected", "true")
				.with("pvps[0].costs[1].id", "idpvp2")
				.with("pvps[0].costs[1].selected", "false")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Pvps de la propuesta")
					.contains("Nombre")
					.contains("Error")
					.contains("Algunos campos tienen nombre no válido");
		});
		
		log.debug("should have validation error of costs");
		webTestClient.post()
		.uri("/consultas/pvpsof/propid/" + propuestaNuestra.getId() + "/edit")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("pvps[0].id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
				.with("pvps[0].name", "nombre actualizado")
				.with("pvps[0].costs[0].id", "idpvp1")
				.with("pvps[0].costs[0].selected", "false")
				.with("pvps[0].costs[1].id", "idpvp2")
				.with("pvps[0].costs[1].selected", "false")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Pvps de la propuesta")
					.contains("Nombre")
					.contains("Error")
					.contains("Debes escoger al menos un coste para cada PVP");
		});
	}
	
	/**
	 * *********************************
	 * COMBINADOS / SUMS
	 * *********************************
	 */
	
	@Test
	void testShowPvpSumsOfProposal() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/pvpsumsof/propid/"+propuestaNuestra.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("PVPs combinados de la propuesta")
						.contains("Nombre")
						.contains(((PropuestaNuestra)propuestaNuestra).getSums().get(0).getName())
						.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName())
						;
			})
			;
	}
	
	@Test
	void testNewPvpSumOfPropuesta() {
		addCosts();
		webTestClient.get()
			.uri("/consultas/pvpsumsof/propid/" + propuestaNuestra.getId() + "/new")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nuevo pvp combinado de la propuesta")
						.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName())
						.contains("Nombre");
			})
			;
	}
	
	@Test
	void testProcessNewPvpSumOfPropuesta() {
		addCosts();
		
		log.debug("should be ok");
		webTestClient.post()
			.uri("/consultas/pvpsumsof/propid/" + propuestaNuestra.getId() + "/new")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("name", ((PropuestaNuestra)propuestaNuestra).getSums().get(0).getName())
					.with("id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
					.with("pvperIds[0]", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId()))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nuevo pvp combinado de la propuesta")
						.contains("Con nombre")
						.contains(((PropuestaNuestra)propuestaNuestra).getSums().get(0).getName());
			})
			;
		
		log.debug("should have name validation error");
		webTestClient.post()
			.uri("/consultas/pvpsumsof/propid/" + propuestaNuestra.getId() + "/new")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("name", "")
					.with("id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
					.with("pvperIds[0]", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId()))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nuevo pvp combinado de la propuesta")
						.contains("Error")
						.contains("Debes seleccionar un nombre")
						.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName());
			})
			;
		
		log.debug("should have pvps validation error");
		webTestClient.post()
			.uri("/consultas/pvpsumsof/propid/" + propuestaNuestra.getId() + "/new")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("name", ((PropuestaNuestra)propuestaNuestra).getSums().get(0).getName())
					.with("id", ((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getId())
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nuevo pvp combinado de la propuesta")
						.contains("Error")
						.contains("Selecciona al menos 1 PVP")
						.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName());
			})
			;
	}

}

