package devs.mrp.gullproject.domains.representationmodels.representationmodelassemblersupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import devs.mrp.gullproject.ValveServiceTestConfig;
import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.domains.representationmodels.MaterialRepresentationModel;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@EnableHypermediaSupport(type = { HypermediaType.HAL, HypermediaType.HAL_FORMS })
@SpringBootTest
@ContextConfiguration(classes = {ValveServiceTestConfig.class})
class MaterialRepresentationModelAssemblerTest {
	
	//@InjectMocks
	//MaterialRepresentationModelAssembler mrma;
	// cuando haces @Mock para insertarlo en la instancia a testear
	
	@Autowired
	MaterialRepresentationModelAssembler mrma;
	// cuando no tiene dependencias
	
	@Test
	void testToModel() {
		
		Material m = new Material();
		m.setName("epdm");
		m.setId("idaleatoria");
		Flux<Material> mFlux = Flux.just(m);
		
		MaterialRepresentationModel mrm = mrma.toModel(m);
		
		assertThat(mrm.getId()).isSameAs(m.getId());
		assertThat(mrm.getName()).isSameAs(m.getName());
		assertThat(mrm.getLink("self").toString()).contains("/api/material/" + m.getId());
		
	}

}
