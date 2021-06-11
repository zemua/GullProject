package devs.mrp.gullproject.service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.dto.WrapAtributosForCampoDto;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
	
	@BeforeEach
	void setup() {
		att1 = new AtributoForCampo();
		att1.setId("att1id");
		att1.setName("nameatt1");
		att1.setTipo("DESCRIPCION");
		att2 = new AtributoForCampo();
		att2.setId("att2id");
		att2.setName("nameatt2");
		att2.setTipo("DESCRIPCION");
		propuesta = new PropuestaCliente();
		propuesta.addAttribute(att1);
		propuesta.addAttribute(att2);
		
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.just(att1, att2));
	}
	
	@Test
	void testWrapAtributos() {
		Mono<WrapAtributosForCampoDto> wrap = propuestaUtilities.wrapAtributos(propuesta.getId());
		// TODO
	}
	
}
