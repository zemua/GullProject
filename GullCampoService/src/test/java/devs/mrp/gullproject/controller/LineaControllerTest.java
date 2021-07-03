package devs.mrp.gullproject.controller;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.gullproject.configuration.MapperConfig;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.dto.linea.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.linea.LineaWithSelectorDto;
import devs.mrp.gullproject.domains.dto.linea.WrapLineasWithSelectorDto;
import devs.mrp.gullproject.domains.dto.propuesta.AtributoForFormDto;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.domains.propuestas.PvperSum;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.AttRemaperUtilities;
import devs.mrp.gullproject.service.CompoundedConsultaLineaService;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.facade.SupplierLineFinderByProposalAssignation;
import devs.mrp.gullproject.service.linea.LineByAssignationRetrieverFactory;
import devs.mrp.gullproject.service.linea.LineaOperations;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.linea.LineaUtilities;
import devs.mrp.gullproject.service.linea.oferta.PvpMapperByLineFactory;
import devs.mrp.gullproject.service.linea.proveedor.CostRemapperUtilities;
import devs.mrp.gullproject.service.propuesta.ProposalIdsMergerFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.FromPropuestaToProveedorFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.PropuestaProveedorExtractor;
import devs.mrp.gullproject.service.propuesta.proveedor.PropuestaProveedorUtilities;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = LineaController.class)
@AutoConfigureWebTestClient
@Import({MapperConfig.class, LineaUtilities.class, AttRemaperUtilities.class, CostRemapperUtilities.class, PropuestaProveedorUtilities.class, PvpMapperByLineFactory.class, SupplierLineFinderByProposalAssignation.class, Consulta.class, ProposalIdsMergerFactory.class, PropuestaProveedorExtractor.class, FromPropuestaToProveedorFactory.class, LineByAssignationRetrieverFactory.class, LineaFactory.class})
class LineaControllerTest {
	
	WebTestClient webTestClient;
	LineaController lineaController;
	ModelMapper modelMapper;
	
	@MockBean
	LineaService lineaService;
	@MockBean
	ConsultaService consultaService;
	@MockBean
	AtributoServiceProxyWebClient atributoService;
	@MockBean CompoundedConsultaLineaService compoundedService;
	
	@Autowired
	public LineaControllerTest(WebTestClient webTestClient, LineaController lineaController, ModelMapper modelMapper) {
		this.webTestClient = webTestClient;
		this.lineaController = lineaController;
		this.modelMapper = modelMapper;
	}
	
	Linea linea1;
	LineaOperations linea1Operations;
	Campo<String> campo1a;
	Campo<Integer> campo1b;
	
	Linea linea2;
	LineaOperations linea2Operations;
	Campo<String> campo2a;
	Campo<Integer> campo2b;
	
	Mono<Linea> mono1;
	Mono<Linea> mono2;
	Flux<Linea> flux;
	
	AtributoForCampo atributo1;
	AtributoForCampo atributo2;
	AtributoForCampo atributo3;
	AtributoForCampo atributo4;
	Flux<AtributoForCampo> fluxAttsPropuesta;
	
	Propuesta propuesta;
	Propuesta propuestaProveedor;
	Propuesta propuestaNuestra;
	Consulta consulta;
	
	CosteProveedor cos1;
	
	@BeforeEach
	void init() {
		atributo1 = new AtributoForCampo();
		atributo1.setId("id1");
		atributo1.setName("atributo1");
		atributo1.setTipo("DESCRIPCION");
		atributo2 = new AtributoForCampo();
		atributo2.setId("id2");
		atributo2.setName("atributo2");
		atributo2.setTipo("NUMERO");
		atributo3 = new AtributoForCampo();
		atributo3.setId("id3");
		atributo3.setName("atributo3");
		atributo3.setTipo("NUMERO");
		atributo4 = new AtributoForCampo();
		atributo4.setId("id4");
		atributo4.setName("atributo4");
		fluxAttsPropuesta = Flux.just(atributo1, atributo2, atributo3, atributo4);
		
		propuesta = new PropuestaCliente();
		propuesta.setNombre("propuestaName");
		consulta = new Consulta();
		consulta.operations().addPropuesta(propuesta);
		
		campo1a = new Campo<>();
		campo1a.setAtributoId(atributo1.getId());
		campo1a.setDatos("datos1");
		campo1b = new Campo<>();
		campo1b.setAtributoId(atributo2.getId());
		campo1b.setDatos(123456789);
		linea1 = new Linea();
		linea1Operations = new LineaOperations(linea1);
		linea1Operations.addCampo(campo1a);
		linea1Operations.addCampo(campo1b);
		linea1.setNombre("nombre linea 1");
		linea1.setPropuestaId(propuesta.getId());
		
		campo2a = new Campo<>();
		campo2a.setAtributoId(atributo2.getId());
		campo2a.setDatos("datos2");
		campo2b = new Campo<>();
		campo2b.setAtributoId(atributo3.getId());
		campo2b.setDatos(321098765);
		linea2 = new Linea();
		linea2Operations = new LineaOperations(linea2);
		linea2Operations.addCampo(campo2a);
		linea2Operations.addCampo(campo2b);
		linea2.setNombre("nombre linea 2");
		linea2.setPropuestaId(propuesta.getId());
		
		linea1.setOrder(0);
		linea2.setOrder(1);
		
		var op = propuesta.operations();
		op.addLineaId(linea1.getId());
		op.addLineaId(linea2.getId());
		op.addAttribute(atributo1);
		op.addAttribute(atributo2);
		
		mono1 = Mono.just(linea1);
		mono2 = Mono.just(linea2);
		flux = Flux.just(linea1, linea2);
		
		
		propuestaProveedor = new PropuestaProveedor(propuesta.getId());
		propuestaProveedor.setAttributeColumns(propuesta.getAttributeColumns());
		propuestaProveedor.setLineaIds(new ArrayList<>());
		propuestaProveedor.getLineaIds().add(propuesta.getLineaIds().get(0));
		propuestaProveedor.getLineaIds().add(propuesta.getLineaIds().get(1));
		propuestaProveedor.setNombre("propuesta proveedor name");
		propuestaProveedor.setForProposalId(propuesta.getId());
		List<CosteProveedor> costes = new ArrayList<>();
		cos1 = new CosteProveedor();
		cos1.setName("COSTE BASE");
		costes.add(cos1);
		((PropuestaProveedor)propuestaProveedor).setCostes(costes);
		
		consulta.getPropuestas().add(propuestaProveedor);
		
		
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(lineaService.addVariasLineas(ArgumentMatchers.any(Flux.class), ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq("NUMERO"))).thenReturn(Mono.just("Integer"));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq("DESCRIPCION"))).thenReturn(Mono.just("String"));
		when(atributoService.validateDataFormat(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Mono.just(false));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo1.getTipo()), ArgumentMatchers.eq(campo1a.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo2.getTipo()), ArgumentMatchers.eq(campo1b.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo1.getTipo()), ArgumentMatchers.eq(campo2a.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo2.getTipo()), ArgumentMatchers.eq(campo2b.getDatosText()))).thenReturn(Mono.just(true));
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(atributo1, atributo2));
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(lineaService.addVariasLineas(ArgumentMatchers.any(Flux.class), ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq("NUMERO"))).thenReturn(Mono.just("Integer"));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq("DESCRIPCION"))).thenReturn(Mono.just("String"));
		when(atributoService.validateDataFormat(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Mono.just(false));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo1.getTipo()), ArgumentMatchers.eq(campo1a.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo2.getTipo()), ArgumentMatchers.eq(campo1b.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo1.getTipo()), ArgumentMatchers.eq(campo2a.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo2.getTipo()), ArgumentMatchers.eq(campo2b.getDatosText()))).thenReturn(Mono.just(true));
		campo2a.setAtributoId(atributo1.getId());
		campo2b.setAtributoId(atributo2.getId());
		
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(atributo1, atributo2));
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(consulta));
		when(lineaService.updateVariasLineas(ArgumentMatchers.any(Flux.class))).thenReturn(Flux.just(linea1, linea2));
		when(atributoService.validateDataFormat(atributo1.getTipo(), campo1a.getDatosText() + "after")).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(atributo1.getTipo(), campo2a.getDatosText() + "after")).thenReturn(Mono.just(true));
		when(lineaService.updateNombre(ArgumentMatchers.eq(linea1.getId()), ArgumentMatchers.eq(campo1b.getDatosText()))).thenReturn(Mono.just(linea1));
		when(lineaService.updateNombre(ArgumentMatchers.eq(linea2.getId()), ArgumentMatchers.eq(campo2b.getDatosText()))).thenReturn(Mono.just(linea2));
		
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(fluxAttsPropuesta);
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.fromIterable(propuestaProveedor.getAttributeColumns()));
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Mono.just(propuestaProveedor));
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Mono.just(consulta));
		
		addCosteToLineas();
	}
	
	private void addCosteToLineas() {
		CosteLineaProveedor cost = new CosteLineaProveedor();
		cost.setValue(123.45);
		cost.setCosteProveedorId(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId());
		List<CosteLineaProveedor> costs = new ArrayList<>();
		costs.add(cost);
		linea1.setCostesProveedor(costs);
		linea2.setCostesProveedor(costs);
		
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(lineaService.findBySeveralPropuestaIds(ArgumentMatchers.anyList())).thenReturn(Flux.just(linea1, linea2));
		when(compoundedService.getAllLineasOfPropuestasAssignedTo(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(compoundedService.getAllLineasOfPropuestasAssignedTo(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.empty());
	}
	
	protected void addPropuestaNuestra() {
		propuestaNuestra = new PropuestaNuestra(propuesta.getId());
		propuestaNuestra.setAttributeColumns(propuesta.getAttributeColumns());
		propuestaNuestra.setLineaIds(new ArrayList<>());
		propuestaNuestra.getLineaIds().add(propuesta.getLineaIds().get(0));
		propuestaNuestra.getLineaIds().add(propuesta.getLineaIds().get(1));
		propuestaNuestra.setNombre("propuesta nuestra name");
		propuestaNuestra.setForProposalId(propuesta.getId());
		List<Pvper> pvps = new ArrayList<>();
		Pvper pvp1 = new Pvper();
		pvp1.setIdCostes(new ArrayList<>() {{add(cos1.getId());}});
		pvp1.setName("pvp1 name");
		pvps.add(pvp1);
		((PropuestaNuestra)propuestaNuestra).setPvps(pvps);
		
		List<PvperSum> sums = new ArrayList<>();
		PvperSum sum1 = new PvperSum();
		sum1.setName("name sum 1");
		sum1.setPvperIds(new ArrayList<>() {{add(pvp1.getId());}});
		sums.add(sum1);
		((PropuestaNuestra)propuestaNuestra).setSums(sums);
		
		PvperLinea pvplinea1 = new PvperLinea();
		pvplinea1.setPvp(45.6);
		pvplinea1.setMargen(6.5);
		pvplinea1.setPvperId(pvp1.getId());
		linea1.setPvps(new ArrayList<>() {{add(pvplinea1);}});
		linea2.setPvps(new ArrayList<>() {{add(pvplinea1);}});
		
		consulta.getPropuestas().add(propuestaNuestra);
		
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuestaNuestra.getId()))).thenReturn(Mono.just(consulta));
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuestaNuestra.getId()))).thenReturn(Flux.just(linea1, linea2));
	}
	
	@Test
	void testShowAllLinesOf() {
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(flux);
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(consulta));
		
		webTestClient.get()
			.uri("/lineas/allof/propid/" + propuesta.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Lineas de la propuesta")
						.contains("Crear nueva linea")
						.contains("Nombre")
						.contains("Enlace")
						.contains(linea1.getCampos().get(0).getDatosText())
						.contains(linea2.getCampos().get(1).getDatosText())
						.contains(propuesta.getNombre());
			});
		
		
		
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(flux);
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Mono.just(propuestaProveedor));
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Mono.just(consulta));
		
		List<CosteLineaProveedor> csts = new ArrayList<>();
		CosteLineaProveedor cst = new CosteLineaProveedor(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId(), 123.45);
		csts.add(cst);
		linea1.setCostesProveedor(csts);
		
		webTestClient.get()
		.uri("/lineas/allof/propid/" + propuestaProveedor.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Lineas de la propuesta")
					.contains("Crear nueva linea")
					.contains("Nombre")
					.contains("Enlace")
					.contains(linea1.getCampos().get(0).getDatosText())
					.contains(linea2.getCampos().get(1).getDatosText())
					.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
					.contains("123.45")
					.contains(propuestaProveedor.getNombre());
		});
	}
	
	@Test
	void testShowAllLinesOfOferta() {
		addPropuestaNuestra();
		webTestClient.get()
		.uri("/lineas/allof/ofertaid/"+propuestaNuestra.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Lineas de la oferta")
					.contains("Nombre")
					.contains("Pvps")
					.contains(linea1.getCampos().get(0).getDatosText())
					.contains(linea2.getCampos().get(1).getDatosText())
					.contains(propuestaNuestra.getNombre())
					.contains(((PropuestaNuestra)propuestaNuestra).getPvps().get(0).getName())
					.contains(((PropuestaNuestra)propuestaNuestra).getSums().get(0).getName())
					.contains(String.valueOf(linea1.getPvps().get(0).getPvp()))
					.contains(String.valueOf(linea2.getPvps().get(0).getPvp()));
		});
	}
	
	@Test
	void testAddLineaToPropuesta() {
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(fluxAttsPropuesta);
		
		webTestClient.get()
		.uri("/lineas/of/" + propuesta.getId() + "/new")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nueva Linea en Propuesta: " + propuesta.getNombre())
					.contains("Nombre")
					.contains("Ok")
					.contains("Volver")
					.contains("atributo1")
					.contains("atributo2")
					.contains("atributo3")
					.contains("atributo4")
					.doesNotContain("COSTES")
					.contains(propuesta.getNombre());
		});
		
		webTestClient.get()
		.uri("/lineas/of/" + propuestaProveedor.getId() + "/new")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nueva Linea en Propuesta: " + propuestaProveedor.getNombre())
					.contains("Nombre")
					.contains("Ok")
					.contains("Volver")
					.contains("atributo1")
					.contains("atributo2")
					//.contains("atributo3")
					//.contains("atributo4")
					.contains("COSTES")
					.contains("COSTE BASE")
					.contains(propuestaProveedor.getNombre());
		});
	}
	
	@Test
	void testProcessAddLineaToPropuesta() {
		List<Linea> lns = new ArrayList<>();
		lns.add(linea1);
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(lineaService.addLinea(ArgumentMatchers.any(Mono.class))).thenReturn(Mono.just(linea1));
		when(lineaService.addVariasLineas(ArgumentMatchers.any(Flux.class), ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.fromIterable(lns));
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.anyString())).thenReturn(Mono.just("String"));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq("DESCRIPCION"), ArgumentMatchers.eq("valor de att 1"))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq("CANTIDAD"), ArgumentMatchers.eq("valor de att 1"))).thenReturn(Mono.just(false));
		
		// all fine
		log.debug("add linea to propuesta primera ronda");
		webTestClient.post()
		.uri("/lineas/of/" + propuesta.getId() + "/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", "nombre")
				.with("linea.id", "lineaid")
				.with("linea.propuestaId", propuesta.getId())
				.with("attributes[0].id", "idatt1")
				.with("attributes[0].value", "valor de att 1")
				.with("attributes[0].localIdentifier", "localIdentifier")
				.with("attributes[0].name", "nombre att 1")
				.with("attributes[0].tipo", "DESCRIPCION")
				.with("qty", "1")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Linea Guardada")
					.contains("Guardando...")
					.contains(String.valueOf(linea1Operations.getCampoByIndex(0).getDatos()))
					.contains(String.valueOf(linea1Operations.getCampoByIndex(1).getDatos()))
					.doesNotContain("errores")
					.contains(propuesta.getNombre())
					.contains(linea1.getNombre())
					.contains("Volver a la propuesta");
		});
		
		// with different ids on the url and the object
		log.debug("add linea to propuesta segunda ronda");
		webTestClient.post()
		.uri("/lineas/of/incorrectid/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", linea1.getNombre())
				.with("linea.id", linea1.getId())
				.with("linea.propuestaId", propuesta.getId())
				.with("attributes[0].id", atributo1.getId())
				.with("attributes[0].value", "valor de att 1")
				.with("attributes[0].localIdentifier", "localIdentifier")
				.with("attributes[0].name", atributo1.getName())
				.with("attributes[0].tipo", "DESCRIPCION")
				.with("qty", "1")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Linea Guardada")
					.doesNotContain("Guardando...")
					.doesNotContain("errores")
					.doesNotContain(propuesta.getNombre())
					.doesNotContain(linea1.getNombre())
					.doesNotContain("Volver a la propuesta")
					.contains("Algo no ha ido bien...");
		});
		
		// with validation error
		log.debug("add linea to propuesta tercera ronda");
		linea1.setNombre("");
		webTestClient.post()
		.uri("/lineas/of/" + propuesta.getId() + "/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", linea1.getNombre())
				.with("linea.id", linea1.getId())
				.with("linea.propuestaId", propuesta.getId())
				.with("attributes[0].id", atributo1.getId())
				.with("attributes[0].value", "valor de att 1")
				.with("attributes[0].localIdentifier", "localIdentifier")
				.with("attributes[0].name", atributo1.getName())
				.with("attributes[0].tipo", "DESCRIPCION")
				.with("qty", "1")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Nueva Linea en Propuesta")
					.doesNotContain("Guardando...")
					.contains("Corrige los errores y reenvía")
					.contains("Selecciona un nombre")
					.contains("Ok")
					.doesNotContain("Volver a la propuesta");
		});
		
		// with validation error
		log.debug("add linea to propuesta con error de validación de atributo");
		linea1.setNombre("valid name");
		webTestClient.post().uri("/lineas/of/" + propuesta.getId() + "/new")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)
				.body(BodyInserters.fromFormData("linea.nombre", linea1.getNombre())
						.with("linea.id", linea1.getId())
						.with("linea.propuestaId", propuesta.getId())
						.with("attributes[0].id", atributo1.getId())
						.with("attributes[0].value", "valor de att 1")
						.with("attributes[0].localIdentifier", "localIdentifier")
						.with("attributes[0].name", atributo1.getName())
						.with("attributes[0].tipo", "CANTIDAD")
						.with("qty", "1"))
				.exchange().expectStatus().isOk().expectBody().consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
							.contains("Gull Project - Nueva Linea en Propuesta")
							.doesNotContain("Guardando...").contains("Nombre:")
							.contains("Corrige los errores y reenvía").contains("El valor no es correcto para este atributo").contains("Ok")
							.doesNotContain("Volver a la propuesta");
				});
		
		when(lineaService.addVariasLineas(ArgumentMatchers.any(Flux.class), ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.fromIterable(lns));
		log.debug("adding linea to propuestaProveedor");
		List<CosteLineaProveedor> csts = new ArrayList<>();
		CosteLineaProveedor cst = new CosteLineaProveedor(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId(), 123.45);
		csts.add(cst);
		linea1.setCostesProveedor(csts);
		webTestClient.post()
		.uri("/lineas/of/" + propuestaProveedor.getId() + "/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", "nombre")
				.with("linea.id", "lineaid")
				.with("linea.propuestaId", propuestaProveedor.getId())
				
				.with("attributes[0].id", "idatt1")
				.with("attributes[0].value", "valor de att 1")
				.with("attributes[0].localIdentifier", "localIdentifier")
				.with("attributes[0].name", "nombre att 1")
				.with("attributes[0].tipo", "DESCRIPCION")
				
				.with("costesProveedor[0].id", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId())
				.with("costesProveedor[0].name", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
				.with("costesProveedor[0].value", "123.45")
				
				.with("qty", "1")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Linea Guardada")
					.contains("Guardando...")
					.contains(String.valueOf(linea1Operations.getCampoByIndex(0).getDatos()))
					.contains(String.valueOf(linea1Operations.getCampoByIndex(1).getDatos()))
					.doesNotContain("errores")
					.contains(propuestaProveedor.getNombre())
					.contains(linea1.getNombre())
					.contains("123.45")
					.contains("Volver a la propuesta");
		});
	}
	
	@Test
	void testRevisarLinea() {
		when(lineaService.findById(ArgumentMatchers.eq(linea1.getId()))).thenReturn(Mono.just(linea1));
		propuesta.getAttributeColumns().clear();
		propuesta.operations().addAttribute(atributo2);
		propuesta.operations().addAttribute(atributo3);
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.fromIterable(propuesta.getAttributeColumns()));
		
		// it shows atributo2 and atributo3 from propuesta, value of atributo2 from linea, and hidden value of atributo1 from linea
		webTestClient.get()
		.uri("/lineas/revisar/id/" + linea1.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Editar Linea")
			.doesNotContain("Corrige los errores y reenvía")
			.contains(linea1.getNombre())
			.doesNotContain(atributo1.getName())
			.contains(propuesta.getAttributeColumns().get(0).getName())
			.contains(propuesta.getAttributeColumns().get(1).getName())
			.contains(String.valueOf(linea1Operations.getCampoByAttId(propuesta.getAttributeColumns().get(0).getId()).getDatos()))
			.contains(String.valueOf(linea1Operations.getCampoByAttId(propuesta.getAttributeColumns().get(1).getId()).getDatos()));
		});
		
		
		log.debug("going to test for propuesta proveedor");
		linea1.setPropuestaId(propuestaProveedor.getId());
		propuestaProveedor.getAttributeColumns().clear();
		propuestaProveedor.operations().addAttribute(atributo2);
		propuestaProveedor.operations().addAttribute(atributo3);
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.fromIterable(propuestaProveedor.getAttributeColumns()));
		
		webTestClient.get()
		.uri("/lineas/revisar/id/" + linea1.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Editar Linea")
			.doesNotContain("Corrige los errores y reenvía")
			.contains(linea1.getNombre())
			.doesNotContain(atributo1.getName())
			.contains(propuestaProveedor.getAttributeColumns().get(0).getName())
			.contains(propuestaProveedor.getAttributeColumns().get(1).getName())
			.contains(String.valueOf(linea1Operations.getCampoByAttId(propuestaProveedor.getAttributeColumns().get(0).getId()).getDatos()))
			.contains(String.valueOf(linea1Operations.getCampoByAttId(propuestaProveedor.getAttributeColumns().get(1).getId()).getDatos()))
			.contains("COSTES")
			.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName());
		});
	}
	
	@Test
	void testProcessRevisarLinea() {
		propuesta.operations().addAttribute(atributo2);
		propuesta.operations().addAttribute(atributo3);
		
		AtributoForLineaFormDto att1 = new AtributoForLineaFormDto();
		att1.setId(atributo1.getId());
		att1.setName(atributo1.getName());
		att1.setTipo(atributo1.getTipo());
		att1.setValue(String.valueOf(linea1Operations.getCampoByIndex(0).getDatos()));
		AtributoForLineaFormDto att2 = new AtributoForLineaFormDto();
		att2.setId(atributo2.getId());
		att2.setName(atributo2.getName());
		att2.setTipo(atributo2.getTipo());
		att2.setValue("85214");
		campo2a.setDatos(att2.getValue());
		AtributoForLineaFormDto att3 = new AtributoForLineaFormDto();
		att3.setId(atributo3.getId());
		att3.setName(atributo3.getName());
		att3.setTipo(atributo3.getTipo());
		att3.setValue("45789");
		campo2b.setDatos(Integer.parseInt(att3.getValue()));
		
		linea1Operations.replaceOrElseAddCampo(atributo2.getId(), campo2a);
		linea1Operations.replaceOrElseAddCampo(atributo3.getId(), campo2b);
		when(lineaService.updateLinea(ArgumentMatchers.any(Linea.class))).thenReturn(Mono.just(linea1));
		
		log.debug("response when all correct");
		when(atributoService.validateDataFormat(att1.getTipo(), att1.getValue())).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(att2.getTipo(), att2.getValue())).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(att3.getTipo(), att3.getValue())).thenReturn(Mono.just(true));
		when(atributoService.getClassTypeOfFormat(att1.getTipo())).thenReturn(Mono.just("String"));
		when(atributoService.getClassTypeOfFormat(att2.getTipo())).thenReturn(Mono.just("Integer"));
		when(atributoService.getClassTypeOfFormat(att3.getTipo())).thenReturn(Mono.just("Integer"));
		
		webTestClient.post()
		.uri("/lineas/revisar/id/" + linea1.getId())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", linea1.getNombre())
				.with("linea.id", linea1.getId())
				.with("linea.propuestaId", linea1.getPropuestaId())
				
				.with("linea.campos[0].id", campo1a.getId())
				.with("linea.campos[0].atributoId", campo1a.getAtributoId())
				.with("linea.campos[0].datos", campo1a.getDatos())
				
				.with("linea.campos[1].id", campo1b.getId())
				.with("linea.campos[1].atributoId", campo1b.getAtributoId())
				.with("linea.campos[1].datos", String.valueOf(campo1b.getDatos()))
				
				.with("attributes[0].id", att2.getId())
				.with("attributes[0].value", att2.getValue())
				.with("attributes[0].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[0].name", att2.getName())
				.with("attributes[0].tipo", att2.getTipo())
				
				.with("attributes[1].id", att3.getId())
				.with("attributes[1].value", att3.getValue())
				.with("attributes[1].localIdentifier", att3.getLocalIdentifier())
				.with("attributes[1].name", att3.getName())
				.with("attributes[1].tipo", att3.getTipo())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Linea Actualizada")
					.contains("Linea Guardada Como...")
					.contains(linea1.getNombre())
					.contains(String.valueOf(linea1Operations.getCampoByIndex(0).getDatos()))
					.contains(String.valueOf(linea1Operations.getCampoByIndex(1).getDatos()))
					.contains(String.valueOf(linea1Operations.getCampoByIndex(2).getDatos()))
					;
		});
		
		
		log.debug("going for propuestaProveedor");
		when(lineaService.updateLinea(ArgumentMatchers.any(Linea.class))).thenReturn(Mono.just(linea1));
		linea1.setPropuestaId(propuestaProveedor.getId());
		List<CosteLineaProveedor> csts = new ArrayList<>();
		CosteLineaProveedor cst = new CosteLineaProveedor(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId(), 123.45);
		csts.add(cst);
		linea1.setCostesProveedor(csts);
		
		webTestClient.post()
		.uri("/lineas/revisar/id/" + linea1.getId())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", linea1.getNombre())
				.with("linea.id", linea1.getId())
				.with("linea.propuestaId", linea1.getPropuestaId())
				
				.with("linea.campos[0].id", campo1a.getId())
				.with("linea.campos[0].atributoId", campo1a.getAtributoId())
				.with("linea.campos[0].datos", campo1a.getDatos())
				
				.with("linea.campos[1].id", campo1b.getId())
				.with("linea.campos[1].atributoId", campo1b.getAtributoId())
				.with("linea.campos[1].datos", String.valueOf(campo1b.getDatos()))
				
				.with("attributes[0].id", att2.getId())
				.with("attributes[0].value", att2.getValue())
				.with("attributes[0].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[0].name", att2.getName())
				.with("attributes[0].tipo", att2.getTipo())
				
				.with("attributes[1].id", att3.getId())
				.with("attributes[1].value", att3.getValue())
				.with("attributes[1].localIdentifier", att3.getLocalIdentifier())
				.with("attributes[1].name", att3.getName())
				.with("attributes[1].tipo", att3.getTipo())
				
				.with("costesProveedor[0].id", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId())
				.with("costesProveedor[0].name", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
				.with("costesProveedor[0].value", "123.45")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Linea Actualizada")
					.contains("Linea Guardada Como...")
					.contains(linea1.getNombre())
					.contains(String.valueOf(linea1Operations.getCampoByIndex(0).getDatos()))
					.contains(String.valueOf(linea1Operations.getCampoByIndex(1).getDatos()))
					.contains(String.valueOf(linea1Operations.getCampoByIndex(2).getDatos()))
					.contains("123.45")
					;
		});
	}
	
	@Test
	void testDeleteLinea() {
		when(lineaService.findById(linea1.getId())).thenReturn(Mono.just(linea1));
		
		webTestClient.get()
		.uri("/lineas/delete/id/" + linea1.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(linea1.getNombre())
			.contains(linea1.getId())
			.contains(linea1Operations.getCampoByIndex(0).getDatosText())
			.contains("Borrar Linea");
		});
		
	}
	
	@Test
	void testProcessDeleteLinea() {
		when(lineaService.deleteLineaById(linea1.getId())).thenReturn(Mono.just(1L));
		webTestClient.post()
		.uri("/lineas/delete/id/" + linea1.getId())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("id", linea1.getId())
			.with("name", linea1.getNombre())
			.with("propuestaId", linea1.getPropuestaId())
					)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Borrar Linea")
			.contains("1 lineas borradas");
		});
	}
	
	@Test
	void testDeleteLineasOf() {
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(flux);
		when(consultaService.findConsultaByPropuestaId(propuesta.getId())).thenReturn(Mono.just(consulta));
		webTestClient.get()
		.uri("/lineas/deleteof/propid/" + propuesta.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(propuesta.getNombre())
			.contains("Lineas de la propuesta")
			.contains("Borrar")
			.contains(linea1.getNombre())
			.contains(linea1Operations.getCampoByIndex(0).getDatosText())
			.contains(linea1Operations.getCampoByIndex(1).getDatosText())
			.contains(linea2.getNombre())
			.contains(linea2Operations.getCampoByIndex(0).getDatosText())
			.contains(linea2Operations.getCampoByIndex(1).getDatosText());
		});
		
		addCosteToLineas();
		webTestClient.get()
		.uri("/lineas/deleteof/propid/" + propuestaProveedor.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(propuestaProveedor.getNombre())
			.contains("Lineas de la propuesta")
			.contains("Borrar")
			.contains(linea1.getNombre())
			.contains(linea1Operations.getCampoByIndex(0).getDatosText())
			.contains(linea1Operations.getCampoByIndex(1).getDatosText())
			.contains(String.valueOf(linea1.getCostesProveedor().get(0).getValue()))
			.contains(linea2.getNombre())
			.contains(linea2Operations.getCampoByIndex(0).getDatosText())
			.contains(linea2Operations.getCampoByIndex(1).getDatosText())
			.contains(String.valueOf(linea2.getCostesProveedor().get(0).getValue()))
			;
		});
	}
	
	@Test
	void testProcessDeleteLinesOf() {
		when(consultaService.findConsultaByPropuestaId(propuesta.getId())).thenReturn(Mono.just(consulta));
		webTestClient.post()
		.uri("/lineas/deleteof/propid/" + propuesta.getId())
		.contentType(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("lineas[0].selected", "true")
				.with("lineas[0].id", linea1.getId())
				.with("lineas[0].nombre", linea1.getNombre())
				.with("lineas[0].propuestaId", linea1.getPropuestaId())
				
				.with("lineas[0].campos[0].id", linea1.getCampos().get(0).getId())
				.with("lineas[0].campos[0].atributoId", linea1.getCampos().get(0).getAtributoId())
				.with("lineas[0].campos[0].datos", linea1.getCampos().get(0).getDatosText())
				
				.with("lineas[0].campos[0].id", linea1.getCampos().get(1).getId())
				.with("lineas[0].campos[0].atributoId", linea1.getCampos().get(1).getAtributoId())
				.with("lineas[0].campos[0].datos", linea1.getCampos().get(1).getDatosText())
				
				.with("lineas[1].selected", "false")
				.with("lineas[1].id", linea2.getId())
				.with("lineas[1].nombre", linea2.getNombre())
				.with("lineas[1].propuestaId", linea2.getPropuestaId())
				
				.with("lineas[1].campos[0].id", linea2.getCampos().get(0).getId())
				.with("lineas[1].campos[0].atributoId", linea2.getCampos().get(0).getAtributoId())
				.with("lineas[1].campos[0].datos", linea2.getCampos().get(0).getDatosText())
				
				.with("lineas[1].campos[0].id", linea2.getCampos().get(1).getId())
				.with("lineas[1].campos[0].atributoId", linea2.getCampos().get(1).getAtributoId())
				.with("lineas[1].campos[0].datos", linea2.getCampos().get(1).getDatosText())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
				.contains("Borrar Líneas")
				.contains(linea1.getNombre())
				.contains(String.valueOf(linea1.getCampos().size()))
				.doesNotContain(linea2.getNombre())
				.doesNotContain(linea1.getCampos().get(0).getDatosText())
				;
		});
		
		addCosteToLineas();
		webTestClient.post()
		.uri("/lineas/deleteof/propid/" + propuestaProveedor.getId())
		.contentType(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("lineas[0].selected", "true")
				.with("lineas[0].id", linea1.getId())
				.with("lineas[0].nombre", linea1.getNombre())
				.with("lineas[0].propuestaId", linea1.getPropuestaId())
				
				.with("lineas[0].campos[0].id", linea1.getCampos().get(0).getId())
				.with("lineas[0].campos[0].atributoId", linea1.getCampos().get(0).getAtributoId())
				.with("lineas[0].campos[0].datos", linea1.getCampos().get(0).getDatosText())
				
				.with("lineas[0].campos[0].id", linea1.getCampos().get(1).getId())
				.with("lineas[0].campos[0].atributoId", linea1.getCampos().get(1).getAtributoId())
				.with("lineas[0].campos[0].datos", linea1.getCampos().get(1).getDatosText())
				
				.with("lineas[0].costesProveedor[0].value", String.valueOf(linea1.getCostesProveedor().get(0).getValue()))
				.with("lineas[0].costesProveedor[0].costeProveedorId", linea1.getCostesProveedor().get(0).getCosteProveedorId())
				
				.with("lineas[1].selected", "false")
				.with("lineas[1].id", linea2.getId())
				.with("lineas[1].nombre", linea2.getNombre())
				.with("lineas[1].propuestaId", linea2.getPropuestaId())
				
				.with("lineas[1].campos[0].id", linea2.getCampos().get(0).getId())
				.with("lineas[1].campos[0].atributoId", linea2.getCampos().get(0).getAtributoId())
				.with("lineas[1].campos[0].datos", linea2.getCampos().get(0).getDatosText())
				
				.with("lineas[1].campos[0].id", linea2.getCampos().get(1).getId())
				.with("lineas[1].campos[0].atributoId", linea2.getCampos().get(1).getAtributoId())
				.with("lineas[1].campos[0].datos", linea2.getCampos().get(1).getDatosText())
				
				.with("lineas[1].costesProveedor[0].value", String.valueOf(linea2.getCostesProveedor().get(0).getValue()))
				.with("lineas[1].costesProveedor[0].costeProveedorId", linea2.getCostesProveedor().get(0).getCosteProveedorId())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
				.contains("Borrar Líneas")
				.contains(linea1.getNombre())
				.contains(String.valueOf(linea1.getCampos().size()))
				.doesNotContain(linea2.getNombre())
				.doesNotContain(linea1.getCampos().get(0).getDatosText())
				;
		});
	}
	
	@Test
	void testProcessConfirmDeleteLinesOf() {
		when(lineaService.deleteVariasLineas(ArgumentMatchers.any(Flux.class))).thenReturn(Mono.empty());
		webTestClient.post()
			.uri("/lineas/deleteof/propid/" + propuesta.getId() + "/confirmed")
			.contentType(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("lineas[0].selected", "true")
					.with("lineas[0].id", linea1.getId())
					.with("lineas[0].nombre", linea1.getNombre())
					.with("lineas[0].propuestaId", linea1.getPropuestaId())
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response ->  {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("Borrar Líneas")
				.contains("Borrado procesado")
				;
			});
	}
	
	@Test
	void testOrderAllLinesOf() {
		propuesta.operations().addAttribute(atributo1);
		propuesta.operations().addAttribute(atributo2);
		propuesta.operations().addAttribute(atributo3);
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(consulta));
		webTestClient.get()
			.uri("/lineas/allof/propid/" + propuesta.getId() + "/order")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(propuesta.getNombre())
				.contains("Ordenar lineas de la propuesta")
				.contains(linea1.getNombre())
				.contains(linea1Operations.getCampoByIndex(0).getDatosText())
				.contains(linea2.getNombre())
				.contains(linea2Operations.getCampoByIndex(1).getDatosText())
				;
			});
	}
	
	@Test
	void testProcessOrderallLinesOf() {
		Map<String, Integer> map = new HashMap<>();
		map.put(linea1.getId(), linea1.getOrder());
		map.put(linea1.getId(), linea1.getOrder());
		when(lineaService.updateOrderOfSeveralLineas(ArgumentMatchers.refEq(map))).thenReturn(Mono.empty());
		
		webTestClient.post()
			.uri("/lineas/allof/propid/" + propuesta.getId() + "/order")
			.contentType(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("lineas[0].id", linea1.getId())
					.with("lineas[0].order", "1")
					.with("lineas[1].id", linea2.getId())
					.with("lineas[1].order", "2")
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Lineas de la propuesta")
					.contains("Orden guardado");
			});
	}
	
	@Test
	void testBulkAddLineasToPropuesta() {
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		
		webTestClient.get()
			.uri("/lineas/of/" + propuesta.getId() + "/bulk-add")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("Nuevas Lineas en Propuesta")
				.contains(propuesta.getNombre())
				.contains("Pega aquí el contenido de tu excel");
			});
	}
	
	@Test
	void testProcessBulkAddLineasToPropuesta() {
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(atributo1, atributo2));
		
		log.debug("should be ok");
		webTestClient.post()
			.uri("/lineas/of/" + propuesta.getId() + "/bulk-add")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("string", "asdf	qwer	zxcv")
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("asdf")
				.contains("qwer")
				.contains("zxcv")
				.contains(propuesta.getAttributeColumns().get(0).getName())
				.contains(propuesta.getAttributeColumns().get(1).getName())
				.contains("mono-name")
				.contains("mono-option");
			});
		
		log.debug("should fail validation");
		webTestClient.post()
		.uri("/lineas/of/" + propuesta.getId() + "/bulk-add")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("string", "")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Corrige los errores y reenvía.")
			.contains("Debes introducir un texto");
		});
		
		log.debug("should show costs in select options");
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Mono.just(propuestaProveedor));
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.just(atributo1, atributo2));
		webTestClient.post()
			.uri("/lineas/of/" + propuestaProveedor.getId() + "/bulk-add")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("string", "asdf	qwer	zxcv")
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("asdf")
				.contains("qwer")
				.contains("zxcv")
				.contains(propuestaProveedor.getAttributeColumns().get(0).getName())
				.contains(propuestaProveedor.getAttributeColumns().get(1).getName())
				.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
				.contains("mono-name")
				.contains("mono-option");
			});
	}
	
	@Test
	void testVerifyBulkAddLineasToPropuesta() {
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(atributo1, atributo2));
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(lineaService.addVariasLineas(ArgumentMatchers.any(Flux.class), ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq("NUMERO"))).thenReturn(Mono.just("Integer"));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq("DESCRIPCION"))).thenReturn(Mono.just("String"));
		when(atributoService.validateDataFormat(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Mono.just(false));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo1.getTipo()), ArgumentMatchers.eq(campo1a.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo2.getTipo()), ArgumentMatchers.eq(campo1b.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo1.getTipo()), ArgumentMatchers.eq(campo2a.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo2.getTipo()), ArgumentMatchers.eq(campo2b.getDatosText()))).thenReturn(Mono.just(true));
		
		log.debug("should be ok");
		webTestClient.post()
			.uri("/lineas/of/" + propuesta.getId() + "/bulk-add/verify")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("name[0]", "")
					.with("name[1]", "2")
					.with("strings[0]", propuesta.getAttributeColumns().get(0).getId())
					.with("strings[1]", propuesta.getAttributeColumns().get(1).getId())
					.with("stringListWrapper[0].string[0]", campo1a.getDatosText())
					.with("stringListWrapper[0].string[1]", campo1b.getDatosText())
					.with("stringListWrapper[1].string[0]", campo2a.getDatosText())
					.with("stringListWrapper[1].string[1]", campo2b.getDatosText())
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(campo1a.getDatosText())
				.contains(campo1b.getDatosText())
				.contains(campo2a.getDatosText())
				.contains(campo2b.getDatosText())
				.contains(linea1.getNombre())
				.contains("Lineas añadidas")
				.doesNotContain("Corrige los errores");
			});
		
		log.debug("should throw validation error of field");
		webTestClient.post()
		.uri("/lineas/of/" + propuesta.getId() + "/bulk-add/verify")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name[0]", "")
				.with("name[1]", "2")
				.with("strings[0]", propuesta.getAttributeColumns().get(0).getId())
				.with("strings[1]", propuesta.getAttributeColumns().get(1).getId())
				.with("stringListWrapper[0].string[0]", campo1a.getDatosText())
				.with("stringListWrapper[0].string[1]", "xxxxx")
				.with("stringListWrapper[1].string[0]", campo2a.getDatosText())
				.with("stringListWrapper[1].string[1]", campo2b.getDatosText())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Corrige los errores")
			.contains("Estos campos tienen un valor incorrecto");
		});
		
		log.debug("should throw validation error of name");
		webTestClient.post()
		.uri("/lineas/of/" + propuesta.getId() + "/bulk-add/verify")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name[0]", "")
				.with("name[1]", "")
				.with("strings[0]", propuesta.getAttributeColumns().get(0).getId())
				.with("strings[1]", propuesta.getAttributeColumns().get(1).getId())
				.with("stringListWrapper[0].string[0]", campo1a.getDatosText())
				.with("stringListWrapper[0].string[1]", campo1b.getDatosText())
				.with("stringListWrapper[1].string[0]", campo2a.getDatosText())
				.with("stringListWrapper[1].string[1]", campo2b.getDatosText())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Corrige los errores")
			.contains("Estas filas necesitan un nombre");
		});
		
		CosteLineaProveedor cost = new CosteLineaProveedor();
		cost.setValue(123.45);
		cost.setCosteProveedorId(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getId());
		List<CosteLineaProveedor> costs = new ArrayList<>();
		costs.add(cost);
		linea1.setCostesProveedor(costs);
		linea2.setCostesProveedor(costs);
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.just(atributo1, atributo2));
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Mono.just(propuestaProveedor));
		when(lineaService.addVariasLineas(ArgumentMatchers.any(Flux.class), ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq("NUMERO"))).thenReturn(Mono.just("Integer"));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.eq("DESCRIPCION"))).thenReturn(Mono.just("String"));
		when(atributoService.validateDataFormat(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Mono.just(false));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo1.getTipo()), ArgumentMatchers.eq(campo1a.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo2.getTipo()), ArgumentMatchers.eq(campo1b.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo1.getTipo()), ArgumentMatchers.eq(campo2a.getDatosText()))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq(atributo2.getTipo()), ArgumentMatchers.eq(campo2b.getDatosText()))).thenReturn(Mono.just(true));
		
		log.debug("should be ok with costs");
		webTestClient.post()
			.uri("/lineas/of/" + propuestaProveedor.getId() + "/bulk-add/verify")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("name[0]", "")
					.with("name[1]", "2")
					.with("name[2]" , "")
					.with("strings[0]", propuesta.getAttributeColumns().get(0).getId())
					.with("strings[1]", propuesta.getAttributeColumns().get(1).getId())
					.with("strings[2]", cost.getCosteProveedorId())
					.with("stringListWrapper[0].string[0]", campo1a.getDatosText())
					.with("stringListWrapper[0].string[1]", campo1b.getDatosText())
					.with("stringListWrapper[0].string[2]", "123.45")
					.with("stringListWrapper[1].string[0]", campo2a.getDatosText())
					.with("stringListWrapper[1].string[1]", campo2b.getDatosText())
					.with("stringListWrapper[1].string[2]", "123.45")
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(campo1a.getDatosText())
				.contains(campo1b.getDatosText())
				.contains(campo2a.getDatosText())
				.contains(campo2b.getDatosText())
				.contains("123.45")
				.contains(linea1.getNombre())
				.contains("Lineas añadidas")
				.doesNotContain("Corrige los errores");
			});
		
		log.debug("should give error on cost validation");
		webTestClient.post()
			.uri("/lineas/of/" + propuestaProveedor.getId() + "/bulk-add/verify")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("name[0]", "")
					.with("name[1]", "2")
					.with("name[2]" , "")
					.with("strings[0]", propuesta.getAttributeColumns().get(0).getId())
					.with("strings[1]", propuesta.getAttributeColumns().get(1).getId())
					.with("strings[2]", cost.getCosteProveedorId())
					.with("stringListWrapper[0].string[0]", campo1a.getDatosText())
					.with("stringListWrapper[0].string[1]", campo1b.getDatosText())
					.with("stringListWrapper[0].string[2]", "incorrect cost")
					.with("stringListWrapper[1].string[0]", campo2a.getDatosText())
					.with("stringListWrapper[1].string[1]", campo2b.getDatosText())
					.with("stringListWrapper[1].string[2]", "123.45")
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("Corrige los errores")
				.contains("Estos campos tienen un valor incorrecto");
			});
	}
	
	@Test
	void testEditAllLinesOf() {		
		webTestClient.get()
			.uri("/lineas/allof/propid/" + propuesta.getId() + "/edit")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(propuesta.getNombre())
				.contains("Editar lineas de la propuesta")
				.contains(linea1.getNombre())
				.contains(linea1Operations.getCampoByIndex(0).getDatosText())
				.contains(linea2.getNombre())
				.contains(linea2Operations.getCampoByIndex(1).getDatosText())
				;
			})
			;
		
		addCosteToLineas();
		webTestClient.get()
		.uri("/lineas/allof/propid/" + propuestaProveedor.getId() + "/edit")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(propuestaProveedor.getNombre())
			.contains("Editar lineas de la propuesta")
			.contains(linea1.getNombre())
			.contains(linea1Operations.getCampoByIndex(0).getDatosText())
			.contains(String.valueOf(linea1.getCostesProveedor().get(0).getValue()))
			.contains(linea2.getNombre())
			.contains(linea2Operations.getCampoByIndex(1).getDatosText())
			.contains(String.valueOf(linea2.getCostesProveedor().get(0).getValue()))
			;
		})
		;
	}
	
	@Test
	void testProcessEditAllLinesOf() {
		log.debug("should be ok");
		webTestClient.post()
			.uri("/lineas/allof/propid/" + propuesta.getId() + "/edit")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("lineaWithAttListDtos[0].linea.nombre", linea1.getNombre())
					.with("lineaWithAttListDtos[0].linea.id", linea1.getId())
					.with("lineaWithAttListDtos[0].linea.propuestaId", linea1.getPropuestaId())
					.with("lineaWithAttListDtos[0].linea.order", String.valueOf(linea1.getOrder()))
					
					.with("lineaWithAttListDtos[1].linea.nombre", linea2.getNombre())
					.with("lineaWithAttListDtos[1].linea.id", linea2.getId())
					.with("lineaWithAttListDtos[1].linea.propuestaId", linea2.getPropuestaId())
					.with("lineaWithAttListDtos[1].linea.order", String.valueOf(linea2.getOrder()))
					
					.with("lineaWithAttListDtos[0].attributes[0].value", campo1a.getDatosText())
					.with("lineaWithAttListDtos[0].attributes[0].localIdentifier", atributo1.getLocalIdentifier())
					.with("lineaWithAttListDtos[0].attributes[0].id", atributo1.getId())
					.with("lineaWithAttListDtos[0].attributes[0].name", atributo1.getName())
					.with("lineaWithAttListDtos[0].attributes[0].tipo", atributo1.getTipo())
					
					.with("lineaWithAttListDtos[0].attributes[1].value", campo1b.getDatosText())
					.with("lineaWithAttListDtos[0].attributes[1].localIdentifier", atributo2.getLocalIdentifier())
					.with("lineaWithAttListDtos[0].attributes[1].id", atributo2.getId())
					.with("lineaWithAttListDtos[0].attributes[1].name", atributo2.getName())
					.with("lineaWithAttListDtos[0].attributes[1].tipo", atributo2.getTipo())
					
					.with("lineaWithAttListDtos[1].attributes[0].value", campo2a.getDatosText())
					.with("lineaWithAttListDtos[1].attributes[0].localIdentifier", atributo1.getLocalIdentifier())
					.with("lineaWithAttListDtos[1].attributes[0].id", atributo1.getId())
					.with("lineaWithAttListDtos[1].attributes[0].name", atributo1.getName())
					.with("lineaWithAttListDtos[1].attributes[0].tipo", atributo1.getTipo())
					
					.with("lineaWithAttListDtos[1].attributes[1].value", campo2b.getDatosText())
					.with("lineaWithAttListDtos[1].attributes[1].localIdentifier", atributo2.getLocalIdentifier())
					.with("lineaWithAttListDtos[1].attributes[1].id", atributo2.getId())
					.with("lineaWithAttListDtos[1].attributes[1].name", atributo2.getName())
					.with("lineaWithAttListDtos[1].attributes[1].tipo", atributo2.getTipo())
					
					.with("lineaWithAttListDtos[0].linea.campos[0].id", campo1a.getId())
					.with("lineaWithAttListDtos[0].linea.campos[0].atributoId", campo1a.getAtributoId())
					.with("lineaWithAttListDtos[0].linea.campos[0].datos", campo1a.getDatosText())
					
					.with("lineaWithAttListDtos[0].linea.campos[1].id", campo1b.getId())
					.with("lineaWithAttListDtos[0].linea.campos[1].atributoId", campo1b.getAtributoId())
					.with("lineaWithAttListDtos[0].linea.campos[1].datos", campo1b.getDatosText())
					
					.with("lineaWithAttListDtos[1].linea.campos[0].id", campo2a.getId())
					.with("lineaWithAttListDtos[1].linea.campos[0].atributoId", campo2a.getAtributoId())
					.with("lineaWithAttListDtos[1].linea.campos[0].datos", campo2a.getDatosText())
					
					.with("lineaWithAttListDtos[1].linea.campos[1].id", campo2b.getId())
					.with("lineaWithAttListDtos[1].linea.campos[1].atributoId", campo2b.getAtributoId())
					.with("lineaWithAttListDtos[1].linea.campos[1].datos", campo2b.getDatosText())
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(linea1.getNombre())
				.contains(linea2.getNombre())
				.contains(campo1a.getDatosText())
				.contains(campo1b.getDatosText())
				.contains(campo2a.getDatosText())
				.contains(campo2b.getDatosText())
				.contains("Editar lineas de la propuesta")
				.doesNotContain("Corrige los errores")
				.doesNotContain("Guardar");
			});
			;
			
			
		log.debug("should fail name validation");
		webTestClient.post()
			.uri("/lineas/allof/propid/" + propuesta.getId() + "/edit")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("lineaWithAttListDtos[0].linea.nombre", "")
					.with("lineaWithAttListDtos[0].linea.id", linea1.getId())
					.with("lineaWithAttListDtos[0].linea.propuestaId", linea1.getPropuestaId())
					.with("lineaWithAttListDtos[0].linea.order", String.valueOf(linea1.getOrder()))
					
					.with("lineaWithAttListDtos[1].linea.nombre", linea2.getNombre())
					.with("lineaWithAttListDtos[1].linea.id", linea2.getId())
					.with("lineaWithAttListDtos[1].linea.propuestaId", linea2.getPropuestaId())
					.with("lineaWithAttListDtos[1].linea.order", String.valueOf(linea2.getOrder()))
					
					.with("lineaWithAttListDtos[0].attributes[0].value", campo1a.getDatosText())
					.with("lineaWithAttListDtos[0].attributes[0].localIdentifier", atributo1.getLocalIdentifier())
					.with("lineaWithAttListDtos[0].attributes[0].id", atributo1.getId())
					.with("lineaWithAttListDtos[0].attributes[0].name", atributo1.getName())
					.with("lineaWithAttListDtos[0].attributes[0].tipo", atributo1.getTipo())
					
					.with("lineaWithAttListDtos[0].attributes[1].value", campo1b.getDatosText())
					.with("lineaWithAttListDtos[0].attributes[1].localIdentifier", atributo2.getLocalIdentifier())
					.with("lineaWithAttListDtos[0].attributes[1].id", atributo2.getId())
					.with("lineaWithAttListDtos[0].attributes[1].name", atributo2.getName())
					.with("lineaWithAttListDtos[0].attributes[1].tipo", atributo2.getTipo())
					
					.with("lineaWithAttListDtos[1].attributes[0].value", campo2a.getDatosText())
					.with("lineaWithAttListDtos[1].attributes[0].localIdentifier", atributo1.getLocalIdentifier())
					.with("lineaWithAttListDtos[1].attributes[0].id", atributo1.getId())
					.with("lineaWithAttListDtos[1].attributes[0].name", atributo1.getName())
					.with("lineaWithAttListDtos[1].attributes[0].tipo", atributo1.getTipo())
					
					.with("lineaWithAttListDtos[1].attributes[1].value", campo2b.getDatosText())
					.with("lineaWithAttListDtos[1].attributes[1].localIdentifier", atributo2.getLocalIdentifier())
					.with("lineaWithAttListDtos[1].attributes[1].id", atributo2.getId())
					.with("lineaWithAttListDtos[1].attributes[1].name", atributo2.getName())
					.with("lineaWithAttListDtos[1].attributes[1].tipo", atributo2.getTipo())
					
					.with("lineaWithAttListDtos[0].linea.campos[0].id", campo1a.getId())
					.with("lineaWithAttListDtos[0].linea.campos[0].atributoId", campo1a.getAtributoId())
					.with("lineaWithAttListDtos[0].linea.campos[0].datos", campo1a.getDatosText())
					
					.with("lineaWithAttListDtos[0].linea.campos[1].id", campo1b.getId())
					.with("lineaWithAttListDtos[0].linea.campos[1].atributoId", campo1b.getAtributoId())
					.with("lineaWithAttListDtos[0].linea.campos[1].datos", campo1b.getDatosText())
					
					.with("lineaWithAttListDtos[1].linea.campos[0].id", campo2a.getId())
					.with("lineaWithAttListDtos[1].linea.campos[0].atributoId", campo2a.getAtributoId())
					.with("lineaWithAttListDtos[1].linea.campos[0].datos", campo2a.getDatosText())
					
					.with("lineaWithAttListDtos[1].linea.campos[1].id", campo2b.getId())
					.with("lineaWithAttListDtos[1].linea.campos[1].atributoId", campo2b.getAtributoId())
					.with("lineaWithAttListDtos[1].linea.campos[1].datos", campo2b.getDatosText())
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.doesNotContain(linea1.getNombre())
				.contains(linea2.getNombre())
				.contains(campo1a.getDatosText())
				.contains(campo1b.getDatosText())
				.contains(campo2a.getDatosText())
				.contains(campo2b.getDatosText())
				.contains("Editar lineas de la propuesta")
				.contains("Corrige los errores")
				.contains("Guardar");
			});
			;
			
			
		log.debug("should fail field validation");
		webTestClient.post()
			.uri("/lineas/allof/propid/" + propuesta.getId() + "/edit")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("lineaWithAttListDtos[0].linea.nombre", linea1.getNombre())
					.with("lineaWithAttListDtos[0].linea.id", linea1.getId())
					.with("lineaWithAttListDtos[0].linea.propuestaId", linea1.getPropuestaId())
					.with("lineaWithAttListDtos[0].linea.order", String.valueOf(linea1.getOrder()))
					
					.with("lineaWithAttListDtos[1].linea.nombre", linea2.getNombre())
					.with("lineaWithAttListDtos[1].linea.id", linea2.getId())
					.with("lineaWithAttListDtos[1].linea.propuestaId", linea2.getPropuestaId())
					.with("lineaWithAttListDtos[1].linea.order", String.valueOf(linea2.getOrder()))
					
					.with("lineaWithAttListDtos[0].attributes[0].value", campo1a.getDatosText())
					.with("lineaWithAttListDtos[0].attributes[0].localIdentifier", atributo1.getLocalIdentifier())
					.with("lineaWithAttListDtos[0].attributes[0].id", atributo1.getId())
					.with("lineaWithAttListDtos[0].attributes[0].name", atributo1.getName())
					.with("lineaWithAttListDtos[0].attributes[0].tipo", atributo1.getTipo())
					
					.with("lineaWithAttListDtos[0].attributes[1].value", "asdf")
					.with("lineaWithAttListDtos[0].attributes[1].localIdentifier", atributo2.getLocalIdentifier())
					.with("lineaWithAttListDtos[0].attributes[1].id", atributo2.getId())
					.with("lineaWithAttListDtos[0].attributes[1].name", atributo2.getName())
					.with("lineaWithAttListDtos[0].attributes[1].tipo", atributo2.getTipo())
					
					.with("lineaWithAttListDtos[1].attributes[0].value", campo2a.getDatosText())
					.with("lineaWithAttListDtos[1].attributes[0].localIdentifier", atributo1.getLocalIdentifier())
					.with("lineaWithAttListDtos[1].attributes[0].id", atributo1.getId())
					.with("lineaWithAttListDtos[1].attributes[0].name", atributo1.getName())
					.with("lineaWithAttListDtos[1].attributes[0].tipo", atributo1.getTipo())
					
					.with("lineaWithAttListDtos[1].attributes[1].value", campo2b.getDatosText())
					.with("lineaWithAttListDtos[1].attributes[1].localIdentifier", atributo2.getLocalIdentifier())
					.with("lineaWithAttListDtos[1].attributes[1].id", atributo2.getId())
					.with("lineaWithAttListDtos[1].attributes[1].name", atributo2.getName())
					.with("lineaWithAttListDtos[1].attributes[1].tipo", atributo2.getTipo())
					
					.with("lineaWithAttListDtos[0].linea.campos[0].id", campo1a.getId())
					.with("lineaWithAttListDtos[0].linea.campos[0].atributoId", campo1a.getAtributoId())
					.with("lineaWithAttListDtos[0].linea.campos[0].datos", campo1a.getDatosText())
					
					.with("lineaWithAttListDtos[0].linea.campos[1].id", campo1b.getId())
					.with("lineaWithAttListDtos[0].linea.campos[1].atributoId", campo1b.getAtributoId())
					.with("lineaWithAttListDtos[0].linea.campos[1].datos", "asdf")
					
					.with("lineaWithAttListDtos[1].linea.campos[0].id", campo2a.getId())
					.with("lineaWithAttListDtos[1].linea.campos[0].atributoId", campo2a.getAtributoId())
					.with("lineaWithAttListDtos[1].linea.campos[0].datos", campo2a.getDatosText())
					
					.with("lineaWithAttListDtos[1].linea.campos[1].id", campo2b.getId())
					.with("lineaWithAttListDtos[1].linea.campos[1].atributoId", campo2b.getAtributoId())
					.with("lineaWithAttListDtos[1].linea.campos[1].datos", campo2b.getDatosText())
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(linea1.getNombre())
				.contains(linea2.getNombre())
				.contains(campo1a.getDatosText())
				.contains("asdf")
				.contains(campo2a.getDatosText())
				.contains(campo2b.getDatosText())
				.contains("Editar lineas de la propuesta")
				.contains("Corrige los errores")
				.contains("Guardar");
			});
			;
					
		addCosteToLineas();
		log.debug("should be ok with costs");
		webTestClient.post()
			.uri("/lineas/allof/propid/" + propuestaProveedor.getId() + "/edit")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("lineaWithAttListDtos[0].linea.nombre", linea1.getNombre())
					.with("lineaWithAttListDtos[0].linea.id", linea1.getId())
					.with("lineaWithAttListDtos[0].linea.propuestaId", linea1.getPropuestaId())
					.with("lineaWithAttListDtos[0].linea.order", String.valueOf(linea1.getOrder()))
					
					.with("lineaWithAttListDtos[1].linea.nombre", linea2.getNombre())
					.with("lineaWithAttListDtos[1].linea.id", linea2.getId())
					.with("lineaWithAttListDtos[1].linea.propuestaId", linea2.getPropuestaId())
					.with("lineaWithAttListDtos[1].linea.order", String.valueOf(linea2.getOrder()))
					
					.with("lineaWithAttListDtos[0].attributes[0].value", campo1a.getDatosText())
					.with("lineaWithAttListDtos[0].attributes[0].localIdentifier", atributo1.getLocalIdentifier())
					.with("lineaWithAttListDtos[0].attributes[0].id", atributo1.getId())
					.with("lineaWithAttListDtos[0].attributes[0].name", atributo1.getName())
					.with("lineaWithAttListDtos[0].attributes[0].tipo", atributo1.getTipo())
					
					.with("lineaWithAttListDtos[0].attributes[1].value", campo1b.getDatosText())
					.with("lineaWithAttListDtos[0].attributes[1].localIdentifier", atributo2.getLocalIdentifier())
					.with("lineaWithAttListDtos[0].attributes[1].id", atributo2.getId())
					.with("lineaWithAttListDtos[0].attributes[1].name", atributo2.getName())
					.with("lineaWithAttListDtos[0].attributes[1].tipo", atributo2.getTipo())
					
					.with("lineaWithAttListDtos[0].costesProveedor[0].value", "963.85")
					.with("lineaWithAttListDtos[0].costesProveedor[0].id", linea1.getCostesProveedor().get(0).getCosteProveedorId())
					.with("lineaWithAttListDtos[0].costesProveedor[0].name", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
					
					.with("lineaWithAttListDtos[1].attributes[0].value", campo2a.getDatosText())
					.with("lineaWithAttListDtos[1].attributes[0].localIdentifier", atributo1.getLocalIdentifier())
					.with("lineaWithAttListDtos[1].attributes[0].id", atributo1.getId())
					.with("lineaWithAttListDtos[1].attributes[0].name", atributo1.getName())
					.with("lineaWithAttListDtos[1].attributes[0].tipo", atributo1.getTipo())
					
					.with("lineaWithAttListDtos[1].attributes[1].value", campo2b.getDatosText())
					.with("lineaWithAttListDtos[1].attributes[1].localIdentifier", atributo2.getLocalIdentifier())
					.with("lineaWithAttListDtos[1].attributes[1].id", atributo2.getId())
					.with("lineaWithAttListDtos[1].attributes[1].name", atributo2.getName())
					.with("lineaWithAttListDtos[1].attributes[1].tipo", atributo2.getTipo())
					
					.with("lineaWithAttListDtos[0].costesProveedor[0].value", "741.85")
					.with("lineaWithAttListDtos[0].costesProveedor[0].id", linea1.getCostesProveedor().get(0).getCosteProveedorId())
					.with("lineaWithAttListDtos[0].costesProveedor[0].name", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
					
					.with("lineaWithAttListDtos[0].linea.campos[0].id", campo1a.getId())
					.with("lineaWithAttListDtos[0].linea.campos[0].atributoId", campo1a.getAtributoId())
					.with("lineaWithAttListDtos[0].linea.campos[0].datos", campo1a.getDatosText())
					
					.with("lineaWithAttListDtos[0].linea.campos[1].id", campo1b.getId())
					.with("lineaWithAttListDtos[0].linea.campos[1].atributoId", campo1b.getAtributoId())
					.with("lineaWithAttListDtos[0].linea.campos[1].datos", campo1b.getDatosText())
					
					.with("lineaWithAttListDtos[1].linea.campos[0].id", campo2a.getId())
					.with("lineaWithAttListDtos[1].linea.campos[0].atributoId", campo2a.getAtributoId())
					.with("lineaWithAttListDtos[1].linea.campos[0].datos", campo2a.getDatosText())
					
					.with("lineaWithAttListDtos[1].linea.campos[1].id", campo2b.getId())
					.with("lineaWithAttListDtos[1].linea.campos[1].atributoId", campo2b.getAtributoId())
					.with("lineaWithAttListDtos[1].linea.campos[1].datos", campo2b.getDatosText())
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(linea1.getNombre())
				.contains(linea2.getNombre())
				.contains(campo1a.getDatosText())
				.contains(campo1b.getDatosText())
				.contains(campo2a.getDatosText())
				.contains(campo2b.getDatosText())
				.contains(String.valueOf(linea1.getCostesProveedor().get(0).getValue()))
				.contains(String.valueOf(linea2.getCostesProveedor().get(0).getValue()))
				.contains("Editar lineas de la propuesta")
				.doesNotContain("Corrige los errores")
				.doesNotContain("Guardar");
			});
			;
	}
	
	@Test
	void testRenameAllLinesOf() {
		webTestClient.get()
			.uri("/lineas/allof/propid/" + propuesta.getId() + "/rename")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(propuesta.getNombre())
				.contains("Renombrar lineas de la propuesta")
				.contains(linea1.getNombre())
				.contains(linea1Operations.getCampoByIndex(0).getDatosText())
				.contains(linea2.getNombre())
				.contains(linea2Operations.getCampoByIndex(1).getDatosText())
				;
			})
			;
		
		
		addCosteToLineas();
		webTestClient.get()
		.uri("/lineas/allof/propid/" + propuestaProveedor.getId() + "/rename")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(propuestaProveedor.getNombre())
			.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
			.contains("Renombrar lineas de la propuesta")
			.contains(linea1.getNombre())
			.contains(linea1Operations.getCampoByIndex(0).getDatosText())
			.contains(String.valueOf(linea1.getCostesProveedor().get(0).getValue()))
			.contains(linea2.getNombre())
			.contains(linea2Operations.getCampoByIndex(1).getDatosText())
			.contains(String.valueOf(linea2.getCostesProveedor().get(0).getValue()))
			;
		})
		;
	}
	
	@Test
	void testProcessRenameAllLinesOf() {
		log.debug("should be ok");
		webTestClient.post()
			.uri("/lineas/allof/propid/" + propuesta.getId() + "/rename")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("name[0]", "")
					.with("name[1]", "2")
					.with("strings[0]", propuesta.getAttributeColumns().get(0).getId())
					.with("strings[1]", propuesta.getAttributeColumns().get(1).getId())
					.with("stringListWrapper[0].id", linea1.getId())
					.with("stringListWrapper[0].string[0]", campo1a.getDatosText())
					.with("stringListWrapper[0].string[1]", campo1b.getDatosText())
					.with("stringListWrapper[1].id", linea2.getId())
					.with("stringListWrapper[1].string[0]", campo2a.getDatosText())
					.with("stringListWrapper[1].string[1]", campo2b.getDatosText())
					)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(campo1a.getDatosText())
				.contains(campo1b.getDatosText())
				.contains(campo2a.getDatosText())
				.contains(campo2b.getDatosText())
				.contains("Renombrar lineas")
				.doesNotContain("Corrige los errores");
			});
		
		log.debug("should throw validation error of name");
		webTestClient.post()
		.uri("/lineas/allof/propid/" + propuesta.getId() + "/rename")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name[0]", "")
				.with("name[1]", "")
				.with("strings[0]", propuesta.getAttributeColumns().get(0).getId())
				.with("strings[1]", propuesta.getAttributeColumns().get(1).getId())
				.with("stringListWrapper[0].id", linea1.getId())
				.with("stringListWrapper[0].string[0]", campo1a.getDatosText())
				.with("stringListWrapper[0].string[1]", campo1b.getDatosText())
				.with("stringListWrapper[1].id", linea2.getId())
				.with("stringListWrapper[1].string[0]", campo2a.getDatosText())
				.with("stringListWrapper[1].string[1]", campo2b.getDatosText())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Corrige los errores")
			.contains("Estas filas necesitan un nombre");
		});
		
		log.debug("should be ok with costs");
		when(lineaService.updateNombre(ArgumentMatchers.eq(linea1.getId()), ArgumentMatchers.eq("123.45"))).thenReturn(Mono.just(linea1));
		when(lineaService.updateNombre(ArgumentMatchers.eq(linea2.getId()), ArgumentMatchers.eq("789.45"))).thenReturn(Mono.just(linea2));
		webTestClient.post()
		.uri("/lineas/allof/propid/" + propuestaProveedor.getId() + "/rename")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name[0]", "")
				.with("name[1]", "")
				.with("name[2]", "2")
				.with("strings[0]", propuestaProveedor.getAttributeColumns().get(0).getId())
				.with("strings[1]", propuestaProveedor.getAttributeColumns().get(1).getId())
				.with("strings[2]", ((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
				.with("stringListWrapper[0].id", linea1.getId())
				.with("stringListWrapper[0].string[0]", campo1a.getDatosText())
				.with("stringListWrapper[0].string[1]", campo1b.getDatosText())
				.with("stringListWrapper[0].string[2]", "123.45")
				.with("stringListWrapper[1].id", linea2.getId())
				.with("stringListWrapper[1].string[0]", campo2a.getDatosText())
				.with("stringListWrapper[1].string[1]", campo2b.getDatosText())
				.with("stringListWrapper[1].string[2]", "789.45")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(campo1a.getDatosText())
			.contains(campo1b.getDatosText())
			.contains(campo2a.getDatosText())
			.contains(campo2b.getDatosText())
			.contains("123.45")
			.contains("789.45")
			.contains("Renombrar lineas")
			.doesNotContain("Corrige los errores");
		});
	}
	
	@Test
	void testRemapValuesGeneral() {
		webTestClient.get()
		.uri("/lineas/allof/propid/" + propuesta.getId() + "/remap")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(propuesta.getNombre())
			.contains(linea1.getNombre())
			.contains(linea1Operations.getCampoByIndex(0).getDatosText())
			.contains(linea2.getNombre())
			.contains(linea2Operations.getCampoByIndex(1).getDatosText())
			;
		})
		;
		
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.just(linea1, linea2));
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Mono.just(consulta));
		addCosteToLineas();
		webTestClient.get()
		.uri("/lineas/allof/propid/" + propuestaProveedor.getId() + "/remap")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(propuestaProveedor.getNombre())
			.contains(linea1.getNombre())
			.contains(String.valueOf(linea1.getCostesProveedor().get(0).getValue()))
			.contains(linea1Operations.getCampoByIndex(0).getDatosText())
			.contains(linea2.getNombre())
			.contains(String.valueOf(linea2.getCostesProveedor().get(0).getValue()))
			.contains(linea2Operations.getCampoByIndex(1).getDatosText())
			.contains(((PropuestaProveedor)propuestaProveedor).getCostes().get(0).getName())
			;
		})
		;
	}
	
	@Test
	void testRemapValuesAttColumn() {
		webTestClient.get()
		.uri("/lineas/allof/propid/" + propuesta.getId() + "/remap/" + atributo1.getLocalIdentifier())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(linea1.getCampos().get(0).getDatosText())
			.contains(linea2.getCampos().get(0).getDatosText())
			.doesNotContain(linea1.getCampos().get(1).getDatosText())
			.doesNotContain(linea2.getCampos().get(1).getDatosText())
			;
		})
		;
	}
	
	@Test
	void testProcessRemapValuesAttColumn() {
		Linea lineaA;
		lineaA = linea1.operations().clonar();
		LineaOperations opa = lineaA.operations();
		opa.getCampoByAttId(atributo1.getId()).setDatosCasting(campo1a.getDatosText() + "after");
		log.debug("linea1: " + linea1.toString());
		log.debug("lineaA: " + lineaA.toString());
		
		Linea lineaB;
		lineaB = linea2.operations().clonar();
		LineaOperations opb = lineaB.operations();
		opb.getCampoByAttId(atributo1.getId()).setDatosCasting(campo2a.getDatosText() + "after");
		log.debug("linea2: " + linea2.toString());
		log.debug("lineaB: " + lineaB.toString());
		
		when(lineaService.updateLinea(ArgumentMatchers.any(Linea.class))).thenReturn(Mono.just(lineaA));
		
		log.debug("should be ok");
		webTestClient.post()
		.uri("/lineas/allof/propid/" + propuesta.getId() + "/remap/" + atributo1.getLocalIdentifier())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("remapers[0].before", campo1a.getDatosText())
				.with("remapers[0].after", campo1a.getDatosText() + "after")
				.with("remapers[0].tipo", atributo1.getTipo())
				.with("remapers[0].name", atributo1.getName())
				.with("remapers[0].atributoId", campo1a.getAtributoId())
				.with("remapers[0].localIdentifier", atributo1.getLocalIdentifier())
				
				.with("remapers[1].after", campo2a.getDatosText() + "after")
				.with("remapers[1].before", campo2a.getDatosText())
				.with("remapers[1].tipo", atributo1.getTipo())
				.with("remapers[1].name", atributo1.getName())
				.with("remapers[1].atributoId", campo2a.getAtributoId())
				.with("remapers[1].localIdentifier", atributo1.getLocalIdentifier())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Remapeado");
		});
		
		
		when(atributoService.validateDataFormat(atributo1.getTipo(), campo2a.getDatosText() + "after")).thenReturn(Mono.just(false));
		log.debug("should give validation error");
		webTestClient.post()
		.uri("/lineas/allof/propid/" + propuesta.getId() + "/remap/" + atributo1.getLocalIdentifier())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("remapers[0].before", campo1a.getDatosText())
				.with("remapers[0].after", campo1a.getDatosText() + "after")
				.with("remapers[0].tipo", atributo1.getTipo())
				.with("remapers[0].name", atributo1.getName())
				.with("remapers[0].atributoId", campo1a.getAtributoId())
				.with("remapers[0].localIdentifier", atributo1.getLocalIdentifier())
				
				.with("remapers[1].after", campo2a.getDatosText() + "after")
				.with("remapers[1].before", campo2a.getDatosText())
				.with("remapers[1].tipo", atributo1.getTipo())
				.with("remapers[1].name", atributo1.getName())
				.with("remapers[1].atributoId", campo2a.getAtributoId())
				.with("remapers[1].localIdentifier", atributo1.getLocalIdentifier())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains(linea1.getCampos().get(0).getDatosText()+"after")
			.contains(linea2.getCampos().get(0).getDatosText()+"after")
			.doesNotContain(linea1.getCampos().get(1).getDatosText())
			.doesNotContain(linea2.getCampos().get(1).getDatosText())
			.contains("error")
			.doesNotContain("Remapeado")
			;
		})
		;
	}
	
	@Test
	void testRemapCosts() {
		addCosteToLineas();
		webTestClient.get()
			.uri("/lineas/allof/propid/"+propuestaProveedor.getId()+"/remapcost/"+linea1.getCostesProveedor().get(0).getCosteProveedorId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains(String.valueOf(linea1.getCostesProveedor().get(0).getValue()))
				.contains(String.valueOf(linea2.getCostesProveedor().get(0).getValue()))
				.doesNotContain(linea1.getCampos().get(1).getDatosText())
				.doesNotContain(linea2.getCampos().get(1).getDatosText())
				;
			})
			;
	}
	
	@Test
	void testProcessRemapCost() {
		addCosteToLineas();
		CosteLineaProveedor coste = linea1.getCostesProveedor().get(0);
		
		Linea lineaA;
		lineaA = new Linea(linea1);
		LineaOperations opa = lineaA.operations();
		opa.getCosteByCosteId(coste.getCosteProveedorId()).setValue(999.99);
		log.debug("linea1: " + linea1.toString());
		log.debug("lineaA: " + lineaA.toString());
		
		Linea lineaB;
		lineaB = new Linea(linea2);
		LineaOperations opb = lineaB.operations();
		opb.getCosteByCosteId(coste.getCosteProveedorId()).setValue(888.88);
		log.debug("linea2: " + linea2.toString());
		log.debug("lineaB: " + lineaB.toString());
		
		when(lineaService.updateLinea(ArgumentMatchers.any(Linea.class))).thenReturn(Mono.just(lineaA));
		
		log.debug("should be ok");
		webTestClient.post()
		.uri("/lineas/allof/propid/" + propuestaProveedor.getId() + "/remapcost/" + coste.getCosteProveedorId())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("remapers[0].before", String.valueOf(linea1.getCostesProveedor().get(0).getValue()))
				.with("remappers[0].after", "999.99")
				.with("remappers[0].costeProveedorId", coste.getCosteProveedorId())
				
				.with("remappers[1].after", "888.88")
				.with("remappers[1].before", String.valueOf(linea2.getCostesProveedor().get(0).getValue()))
				.with("remappers[1].costeProveedorId", coste.getCosteProveedorId())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Remapeado");
		});
		
		log.debug("no test with validation error, because if it is not a valid number it just jumps it");
	}
	
	@Test
	void testAssignCounterLine() {
		addCosteToLineas();
		webTestClient.get()
			.uri("/lineas/allof/propid/"+propuestaProveedor.getId()+"/counter-assign")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("La propuesta tiene el mismo número de líneas")
				.contains("Asignar por posicion")
				.contains("Todas las líneas de esta propuesta que comparten nombre tienen los mismos costes")
				.contains("Asignar por nombre")
				.doesNotContain("La propuesta no tiene el mismo número de líneas")
				.doesNotContain("Hay algunas líneas de esta propuesta que comparten nombre y no tienen lo mismos costes")
				;
			})
			;
		
		propuestaProveedor.getLineaIds().add("otra");
		webTestClient.get()
		.uri("/lineas/allof/propid/"+propuestaProveedor.getId()+"/counter-assign")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.doesNotContain("La propuesta tiene el mismo número de líneas")
			.doesNotContain("Asignar por posicion")
			.contains("Todas las líneas de esta propuesta que comparten nombre tienen los mismos costes")
			.contains("Asignar por nombre")
			.contains("La propuesta no tiene el mismo número de líneas")
			.doesNotContain("Hay algunas líneas de esta propuesta que comparten nombre y no tienen lo mismos costes")
			;
		})
		;
		
		Linea lineaco = new Linea();
		lineaco.setCampos(linea1.getCampos());
		lineaco.setCostesProveedor(new ArrayList<>());
		lineaco.getCostesProveedor().add(new CosteLineaProveedor(linea1.getCostesProveedor().get(0)));
		lineaco.getCostesProveedor().get(0).setValue(12369.85);
		lineaco.setNombre(linea1.getNombre());
		lineaco.setPropuestaId(propuestaProveedor.getId());
		
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuestaProveedor.getId()))).thenReturn(Flux.just(linea1, linea2, lineaco));
		
		webTestClient.get()
		.uri("/lineas/allof/propid/"+propuestaProveedor.getId()+"/counter-assign")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.doesNotContain("La propuesta tiene el mismo número de líneas")
			.doesNotContain("Asignar por posicion")
			.doesNotContain("Todas las líneas de esta propuesta que comparten nombre tienen los mismos costes")
			.doesNotContain("Asignar por nombre")
			.contains("La propuesta no tiene el mismo número de líneas")
			.contains("Hay algunas líneas de esta propuesta que comparten nombre y no tienen lo mismos costes")
			;
		})
		;
	}
	
	@Test
	void testProcessAssignCounterLineByOrder() {
		addCosteToLineas();
		
		Linea lineawithcounter = new Linea(linea1);
		List<String> list = new ArrayList<>();
		list.add("counterLineId");
		lineawithcounter.setCounterLineId(list);
		when(lineaService.updateCounterLineId(ArgumentMatchers.eq(linea1.getId()), ArgumentMatchers.anyList())).thenReturn(Mono.just(lineawithcounter));
		when(lineaService.updateCounterLineId(ArgumentMatchers.eq(linea2.getId()), ArgumentMatchers.anyList())).thenReturn(Mono.just(lineawithcounter));
		
		webTestClient.post()
		.uri("/lineas/allof/propid/" + propuestaProveedor.getId() + "/counter-line")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("id", propuestaProveedor.getId())
				.with("nombre", propuestaProveedor.getNombre())
				.with("tipoPropuesta", propuestaProveedor.getTipoPropuesta().toString())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Asignadas");
		});
	}
	
	@Test
	void testProcessAssignCounterLineByName() {
		addCosteToLineas();
		
		Linea lineawithcounter = new Linea(linea1);
		List<String> list = new ArrayList<>();
		list.add("counterLineId");
		lineawithcounter.setCounterLineId(list);
		when(lineaService.updateCounterLineId(ArgumentMatchers.eq(linea1.getId()), ArgumentMatchers.anyList())).thenReturn(Mono.just(lineawithcounter));
		when(lineaService.updateCounterLineId(ArgumentMatchers.eq(linea2.getId()), ArgumentMatchers.anyList())).thenReturn(Mono.just(lineawithcounter));
		when(lineaService.addCounterLineId(ArgumentMatchers.eq(linea1.getId()), ArgumentMatchers.anyString())).thenReturn(Mono.just(lineawithcounter));
		when(lineaService.addCounterLineId(ArgumentMatchers.eq(linea2.getId()), ArgumentMatchers.anyString())).thenReturn(Mono.just(lineawithcounter));
		
		webTestClient.post()
		.uri("/lineas/allof/propid/" + propuestaProveedor.getId() + "/counter-name")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("id", propuestaProveedor.getId())
				.with("nombre", propuestaProveedor.getNombre())
				.with("tipoPropuesta", propuestaProveedor.getTipoPropuesta().toString())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Asignadas");
		});
	}

}
