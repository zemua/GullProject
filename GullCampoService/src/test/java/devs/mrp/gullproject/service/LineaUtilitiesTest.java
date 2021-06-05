package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
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
		att2 = new AtributoForCampo();
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
			})
			.expectComplete()
			.verify()
			;
	}

}
