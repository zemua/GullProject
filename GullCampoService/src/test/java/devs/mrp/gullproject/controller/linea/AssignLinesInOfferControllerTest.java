package devs.mrp.gullproject.controller.linea;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstractaFactory;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.domains.propuestas.Pvper.IdAttsList;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaOfertaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaOfferService;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//@WebFluxTest(controllers = AssignLinesInOfferController.class)
@AutoConfigureWebTestClient
//@Import({LineaUtilities.class, AttRemaper.class, ModelMapper.class, CompoundedConsultaLineaService.class, LineaFactory.class, AttRemaperUtilities.class, CostRemapperUtilities.class, PropuestaProveedorUtilities.class, SupplierLineFinderByProposalAssignation.class})
@ActiveProfiles("default")
class AssignLinesInOfferControllerTest {
	
	@Autowired WebTestClient webTestClient;
	@Autowired LineaController lineaController;
	@Autowired LineaAbstractaFactory lineaAbstractaFactory;
	
	@Autowired LineaOfferService lineaOfferService;
	@Autowired LineaService lineaService;
	@Autowired ConsultaService consultaService;
	@MockBean AtributoServiceProxyWebClient atributoService;
	
	@Autowired LineaRepo lineaRepo;
	@Autowired LineaOfertaRepo lineaOfertaRepo;
	@Autowired ConsultaRepo consultaRepo;

	Consulta consulta;
	PropuestaNuestra propuestaNuestra;
	LineaAbstracta linea1;
	LineaAbstracta linea2;
	LineaAbstracta linea3;
	LineaAbstracta linea4;
	PropuestaProveedor propuestaProveedor;
	Linea lineaProveedor1;
	Linea lineaProveedor2;
	PropuestaCliente propuestaCliente;
	Linea lineaCliente1;
	Linea lineaCliente2;
	
	@BeforeEach
	void setup() {
		// CLEAN
		lineaRepo.deleteAll().block();
		lineaOfertaRepo.deleteAll().block();
		consultaRepo.deleteAll().block();
		
		// CONSULTA CLIENTE
		propuestaCliente = new PropuestaCliente();
		AtributoForCampo col1 = new AtributoForCampo();
		col1.setName("name att 1");
		col1.setId("att1");
		AtributoForCampo col2 = new AtributoForCampo();
		col2.setName("name att 2");
		col2.setId("att2");
		propuestaCliente.setAttributeColumns(new ArrayList<>() {{add(col1);add(col2);}});
		
		lineaCliente1 = new Linea();
		lineaCliente1.setNombre("nombre linea cliente 1");
		lineaCliente1.setPropuestaId(propuestaCliente.getId());
		Campo<String> campo1 = new Campo<>();
		campo1.setAtributoId("att1");
		campo1.setDatos("datos campo 1 de linea 1");
		Campo<String> campo2 = new Campo<>();
		campo2.setAtributoId("att2");
		campo2.setDatos("datos campo 2 de linea 1");
		lineaCliente1.setCampos(new ArrayList<>() {{add(campo1);add(campo2);}});
		
		lineaCliente2 = new Linea();
		lineaCliente2.setNombre("nombre linea cliente 2");
		lineaCliente2.setPropuestaId(propuestaCliente.getId());
		Campo<String> campo3 = new Campo<>();
		campo3.setAtributoId("att1");
		campo3.setDatos("datos campo 3 de linea 2");
		Campo<String> campo4 = new Campo<>();
		campo4.setAtributoId("att2");
		campo4.setDatos("datos campo 4 de linea 2");
		lineaCliente2.setCampos(new ArrayList<>() {{add(campo3);add(campo4);}});
		
		lineaService.addLinea(lineaCliente1).block();
		lineaService.addLinea(lineaCliente2).block();
		
		// PROPUESTA PROVEEDOR
		propuestaProveedor = new PropuestaProveedor();
		propuestaProveedor.setForProposalId(propuestaCliente.getId());
		propuestaProveedor.setAttributeColumns(new ArrayList<>() {{add(col1);add(col2);}});
		CosteProveedor c1 = new CosteProveedor();
		c1.setName("coste propuesta 1");
		CosteProveedor c2 = new CosteProveedor();
		c2.setName("coste propuesta 2");
		propuestaProveedor.setCostes(new ArrayList<>() {{add(c1);add(c2);}});
		
		lineaProveedor1 = new Linea();
		lineaProveedor1.setPropuestaId(propuestaProveedor.getId());
		lineaProveedor1.setCounterLineId(new ArrayList<>() {{add(lineaCliente1.getId());}});
		lineaProveedor1.setCampos(new ArrayList<>() {{add(campo1);add(campo2);}});
		CosteLineaProveedor cost1 = new CosteLineaProveedor();
		cost1.setCosteProveedorId(c1.getId());
		cost1.setValue(9);
		CosteLineaProveedor cost2 = new CosteLineaProveedor();
		cost2.setCosteProveedorId(c2.getId());
		cost2.setValue(8);
		lineaProveedor1.setCostesProveedor(new ArrayList<>() {{add(cost1);add(cost2);}});
		
		lineaProveedor2 = new Linea();
		lineaProveedor2.setPropuestaId(propuestaProveedor.getId());
		lineaProveedor2.setCounterLineId(new ArrayList<>() {{add(lineaCliente2.getId());}});
		lineaProveedor2.setCampos(new ArrayList<>() {{add(campo1);add(campo2);}});
		CosteLineaProveedor cost3 = new CosteLineaProveedor();
		cost3.setCosteProveedorId(c1.getId());
		cost3.setValue(7);
		CosteLineaProveedor cost4 = new CosteLineaProveedor();
		cost4.setCosteProveedorId(c2.getId());
		cost4.setValue(6);
		lineaProveedor2.setCostesProveedor(new ArrayList<>() {{add(cost3);add(cost4);}});
		
		lineaService.addLinea(lineaProveedor1).block();
		lineaService.addLinea(lineaProveedor2).block();
		
		// OFERTA
		propuestaNuestra = new PropuestaNuestra();
		propuestaNuestra.setNombre("propuesta nuestra nombre");
		propuestaNuestra.setForProposalId(propuestaCliente.getId());
		propuestaNuestra.setAttributeColumns(new ArrayList<>() {{add(col1);add(col2);}});
		Pvper pvper1 = new Pvper();
		pvper1.setName("nombre pvper 1");
		pvper1.setIdCostes(new ArrayList<>() {{add(c1.getId());}});
		IdAttsList attlist = new IdAttsList();
		attlist.setCotizId(propuestaProveedor.getId());
		attlist.setIds(List.of(col1.getId(), col2.getId()));
		pvper1.setIdAttributesByCotiz(List.of(attlist));
		Pvper pvper2 = new Pvper();
		pvper2.setName("nombre pvper 2");
		pvper2.setIdCostes(new ArrayList<>() {{add(c2.getId());}});
		pvper2.setIdAttributesByCotiz(List.of(attlist));
		propuestaNuestra.setPvps(new ArrayList<>() {{add(pvper1);add(pvper2);}});
		
		linea1 = lineaAbstractaFactory.create();
		linea1.setPropuestaId(propuestaNuestra.getId());
		linea1.setCounterLineId(lineaCliente1.getId());
		linea1.setNombre("linea oferta 1");
		PvperLinea pvpl1 = new PvperLinea();
		pvpl1.setPvp(5555);
		pvpl1.setMargen(55);
		pvpl1.setPvperId(pvper1.getId());
		linea1.setPvp(pvpl1);
		
		linea2 = lineaAbstractaFactory.create();
		linea2.setPropuestaId(propuestaNuestra.getId());
		linea2.setCounterLineId(lineaCliente2.getId());
		linea2.setNombre("linea oferta 2");
		PvperLinea pvpl2 = new PvperLinea();
		pvpl2.setPvp(6666);
		pvpl2.setMargen(66);
		pvpl2.setPvperId(pvper1.getId());
		linea2.setPvp(pvpl2);
		
		linea3 = lineaAbstractaFactory.create();
		linea3.setPropuestaId(propuestaNuestra.getId());
		linea3.setCounterLineId(lineaCliente1.getId());
		linea3.setNombre("linea oferta 3");
		PvperLinea pvpl3 = new PvperLinea();
		pvpl3.setPvp(7777);
		pvpl3.setMargen(77);
		pvpl3.setPvperId(pvper2.getId());
		linea3.setPvp(pvpl3);
		
		linea4 = lineaAbstractaFactory.create();
		linea4.setPropuestaId(propuestaNuestra.getId());
		linea4.setCounterLineId(lineaCliente2.getId());
		linea4.setNombre("linea oferta 4");
		PvperLinea pvpl4 = new PvperLinea();
		pvpl4.setPvp(8888);
		pvpl4.setMargen(88);
		pvpl4.setPvperId(pvper2.getId());
		linea4.setPvp(pvpl4);
		
		Campo<String> c1a = new Campo<>();
		c1a.setAtributoId(propuestaNuestra.getAttributeColumns().get(0).getId());
		c1a.setDatos("datos 1a");
		Campo<String> c1b = new Campo<>();
		c1b.setAtributoId(propuestaNuestra.getAttributeColumns().get(1).getId());
		c1b.setDatos("datos 1b");
		linea1.setCampos(List.of(c1a, c1b));
		
		Campo<String> c2a = new Campo<>();
		c2a.setAtributoId(propuestaNuestra.getAttributeColumns().get(0).getId());
		c2a.setDatos("datos 2a");
		Campo<String> c2b = new Campo<>();
		c2b.setAtributoId(propuestaNuestra.getAttributeColumns().get(1).getId());
		c2b.setDatos("datos 2b");
		linea2.setCampos(List.of(c2a, c2b));
		
		Campo<String> c3a = new Campo<>();
		c3a.setAtributoId(propuestaNuestra.getAttributeColumns().get(0).getId());
		c3a.setDatos("datos 3a");
		Campo<String> c3b = new Campo<>();
		c3b.setAtributoId(propuestaNuestra.getAttributeColumns().get(1).getId());
		c3b.setDatos("datos 3b");
		linea3.setCampos(List.of(c3a, c3b));
		
		Campo<String> c4a = new Campo<>();
		c4a.setAtributoId(propuestaNuestra.getAttributeColumns().get(0).getId());
		c4a.setDatos("datos 4a");
		Campo<String> c4b = new Campo<>();
		c4b.setAtributoId(propuestaNuestra.getAttributeColumns().get(1).getId());
		c4b.setDatos("datos 4b");
		linea4.setCampos(List.of(c4a, c4b));
		
		lineaOfferService.addLinea(linea1).block();
		lineaOfferService.addLinea(linea2).block();
		lineaOfferService.addLinea(linea3).block();
		lineaOfferService.addLinea(linea4).block();
		
		// CONSULTA
		consulta = new Consulta();
		consulta.setPropuestas(new ArrayList<>() {{add(propuestaCliente);add(propuestaProveedor);add(propuestaNuestra);}});
		
		consultaService.save(consulta).block();
	}
	
	@AfterEach
	void cleanup() {
		lineaRepo.deleteAll().block();
		lineaOfertaRepo.deleteAll().block();
		consultaRepo.deleteAll().block();
	}
	
	@Test
	@WithMockUser
	void testShowAllLinesOfOferta() {
		
		webTestClient.get()
			.uri("/lineas/allof/ofertaid/" + propuestaNuestra.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("Lineas de la oferta")
				.contains(propuestaNuestra.getNombre())
				.contains(propuestaCliente.getAttributeColumns().get(0).getName())
				.contains(propuestaCliente.getAttributeColumns().get(1).getName())
				.contains(lineaCliente1.getNombre())
				.contains(lineaCliente2.getNombre())
				.contains(lineaCliente1.getCampos().get(0).getDatosText())
				.contains(lineaCliente2.getCampos().get(1).getDatosText())
				/*.contains(String.valueOf(linea1.getPvp().getPvp()))
				.contains(String.valueOf(linea2.getPvp().getPvp()))
				.contains(String.valueOf(linea3.getPvp().getPvp()))
				.contains(String.valueOf(linea4.getPvp().getPvp()))*/
				// decimal format "5555,00"
				.contains("5555,00")
				.contains("6666,00")
				.contains("7777,00")
				.contains("8888,00")
				;
			})
			;
	}
	
	@Test
	@WithMockUser
	void testAssignLinesOfOferta() {
		webTestClient.get()
			.uri("/lineas/allof/ofertaid/" + propuestaNuestra.getId() + "/assign")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Asignar lineas de la oferta")
					.contains(propuestaNuestra.getNombre())
					.contains(lineaCliente1.getNombre())
					.contains(lineaCliente2.getNombre())
					.contains(lineaCliente1.getCampos().get(0).getDatosText())
					.contains(lineaCliente2.getCampos().get(1).getDatosText())
					.contains(String.valueOf(linea1.getPvp().getPvp()))
					.contains(String.valueOf(linea2.getPvp().getPvp()))
					.contains(String.valueOf(linea3.getPvp().getPvp()))
					.contains(String.valueOf(linea4.getPvp().getPvp()))
					.contains("16") // total cost of pvp1
					.contains("14") // total cost of pvp2
					;
			})
			;
	}
	
	@Test
	@WithMockUser
	void testProcessAssignLinesOfOferta() {
		webTestClient.post()
		.uri("/lineas/allof/ofertaid/" + propuestaNuestra.getId() + "/assign")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("lineas[0].pvp.margen", "2.91")
				.with("lineas[0].pvp.pvp", "263.12")
				.with("lineas[0].pvp.pvperId", propuestaNuestra.getPvps().get(0).getId())
				.with("lineas[0].selected", "true")
				.with("lineas[0].id", "idlinea1")
				.with("lineas[0].nombre", "")
				.with("lineas[0].propuestaId", propuestaNuestra.getId())
				.with("lineas[0].parentId", "")
				.with("lineas[0].counterLineId", lineaCliente1.getId())
				.with("lineas[0].order", "0")
				.with("lineas[0].qty", "")
				/*.with("lineas[0].campos[0].id", "campo1aid")
				.with("lineas[0].campos[0].atributoId", propuestaCliente.getAttributeColumns().get(0).getId())
				.with("lineas[0].campos[0].datos", "datos campo 1a")
				.with("lineas[0].campos[1].id", "campo1bid")
				.with("lineas[0].campos[1].atributoId", propuestaCliente.getAttributeColumns().get(1).getId())
				.with("lineas[0].campos[1].datos", "datos campo 1b")*/
				
				.with("lineas[1].pvp.margen", "3.91")
				.with("lineas[1].pvp.pvp", "264.12")
				.with("lineas[1].pvp.pvperId", propuestaNuestra.getPvps().get(1).getId())
				.with("lineas[1].selected", "true")
				.with("lineas[1].id", "idlinea2")
				.with("lineas[1].nombre", "")
				.with("lineas[1].propuestaId", propuestaNuestra.getId())
				.with("lineas[1].counterLineId", lineaCliente2.getId())
				/*.with("lineas[1].campos[0].id", "campo2aid")
				.with("lineas[1].campos[0].atributoId", propuestaCliente.getAttributeColumns().get(0).getId())
				.with("lineas[1].campos[0].datos", "datos campo 2a")
				.with("lineas[1].campos[1].id", "campo2bid")
				.with("lineas[1].campos[1].atributoId", propuestaCliente.getAttributeColumns().get(1).getId())
				.with("lineas[1].campos[1].datos", "datos campo 2b") */
				
				.with("lineas[2].pvp.margen", "4.91")
				.with("lineas[2].pvp.pvp", "265.12")
				.with("lineas[2].pvp.pvperId", propuestaNuestra.getPvps().get(0).getId())
				.with("lineas[2].selected", "true")
				.with("lineas[2].id", "idlinea3")
				.with("lineas[2].nombre", "")
				.with("lineas[2].propuestaId", propuestaNuestra.getId())
				.with("lineas[2].counterLineId", lineaCliente1.getId())
				/*.with("lineas[2].campos[0].id", "campo3aid")
				.with("lineas[2].campos[0].atributoId", propuestaCliente.getAttributeColumns().get(0).getId())
				.with("lineas[2].campos[0].datos", "datos campo 3a")
				.with("lineas[2].campos[1].id", "campo3bid")
				.with("lineas[2].campos[1].atributoId", propuestaCliente.getAttributeColumns().get(1).getId())
				.with("lineas[2].campos[1].datos", "datos campo 3b")*/
				
				.with("lineas[3].pvp.margen", "5.91")
				.with("lineas[3].pvp.pvp", "266.12")
				.with("lineas[3].pvp.pvperId", propuestaNuestra.getPvps().get(1).getId())
				.with("lineas[3].selected", "false")
				.with("lineas[3].id", "idlinea4")
				.with("lineas[3].nombre", "")
				.with("lineas[3].propuestaId", propuestaNuestra.getId())
				.with("lineas[3].counterLineId", lineaCliente2.getId())
				/*.with("lineas[3].campos[0].id", "campo4aid")
				.with("lineas[3].campos[0].atributoId", propuestaCliente.getAttributeColumns().get(0).getId())
				.with("lineas[3].campos[0].datos", "datos campo 4a")
				.with("lineas[3].campos[1].id", "campo4bid")
				.with("lineas[3].campos[1].atributoId", propuestaCliente.getAttributeColumns().get(1).getId())
				.with("lineas[3].campos[1].datos", "datos campo 4b")*/
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Asignar lineas de la oferta")
					.contains("Guardando...")
					.contains("263.12")
					.contains("2.91")
					.contains("3.91")
					.contains("264.12")
					.contains("4.91")
					.contains("265.12")
					.doesNotContain("5.91")
					.doesNotContain("266.12")
					.contains("datos campo 1 de linea 1")
					.contains("datos campo 2 de linea 1")
					;
		});
	}
	
	@Test
	@WithMockUser
	void testExportLinesOfOferta() {
		webTestClient.get()
			.uri("/lineas/allof/ofertaid/" + propuestaNuestra.getId() + "/export")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
				.contains("Exportar lineas de la oferta")
				.contains(propuestaNuestra.getNombre())
				.contains(propuestaCliente.getAttributeColumns().get(0).getName())
				.contains(propuestaCliente.getAttributeColumns().get(1).getName())
				.contains(lineaCliente1.getNombre())
				.contains(lineaCliente2.getNombre())
				.contains(linea1.getCampos().get(0).getDatosText())
				.contains(linea2.getCampos().get(1).getDatosText())
				.contains(linea3.getCampos().get(0).getDatosText())
				.contains(linea4.getCampos().get(1).getDatosText())
				/*.contains(String.valueOf(linea1.getPvp().getPvp()))
				.contains(String.valueOf(linea2.getPvp().getPvp()))
				.contains(String.valueOf(linea3.getPvp().getPvp()))
				.contains(String.valueOf(linea4.getPvp().getPvp()))*/
				// in decimal format 12,43
				.contains("5555,00")
				.contains("6666,00")
				.contains("7777,00")
				.contains("8888,00")
				;
			})
			;
	}

}
