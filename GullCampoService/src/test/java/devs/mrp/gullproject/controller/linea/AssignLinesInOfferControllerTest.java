package devs.mrp.gullproject.controller.linea;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstractaFactory;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaOfferService;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AssignLinesInOfferController.class)
@AutoConfigureWebTestClient
class AssignLinesInOfferControllerTest {
	
	@Autowired WebTestClient webTestClient;
	@Autowired LineaController lineaController;
	@Autowired LineaAbstractaFactory lineaAbstractaFactory;
	
	@MockBean LineaOfferService lineaOfferService;
	@MockBean LineaService lineaService;
	@MockBean ConsultaService consultaService;

	PropuestaNuestra propuestaNuestra;
	
	PropuestaCliente propuestaCliente;
	Linea lineaCliente1;
	Linea lineaCliente2;
	
	@BeforeEach
	void setup() {
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
		
		// OFERTA
		propuestaNuestra = new PropuestaNuestra();
		
		Pvper pvper1 = new Pvper();
		pvper1.setName("nombre pvper 1");
		Pvper pvper2 = new Pvper();
		pvper2.setName("nombre pvper 2");
		propuestaNuestra.setPvps(new ArrayList<>() {{add(pvper1);add(pvper2);}});
		
		var linea1 = lineaAbstractaFactory.create();
		linea1.setPropuestaId(propuestaNuestra.getId());
		linea1.setCounterLineId(lineaCliente1.getId());
		linea1.setNombre("linea oferta 1");
		PvperLinea pvpl1 = new PvperLinea();
		pvpl1.setPvp(5);
		pvpl1.setPvperId(pvper1.getId());
		linea1.setPvp(pvpl1);
		
		var linea2 = lineaAbstractaFactory.create();
		linea2.setPropuestaId(propuestaNuestra.getId());
		linea2.setCounterLineId(lineaCliente2.getId());
		linea2.setNombre("linea oferta 2");
		PvperLinea pvpl2 = new PvperLinea();
		pvpl2.setPvp(6);
		pvpl2.setPvperId(pvper1.getId());
		linea2.setPvp(pvpl2);
		
		var linea3 = lineaAbstractaFactory.create();
		
		var linea4 = lineaAbstractaFactory.create();
	}
	
	@Test
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
				;
			})
			;
		
		fail("Not yet implemented");
	}

}
