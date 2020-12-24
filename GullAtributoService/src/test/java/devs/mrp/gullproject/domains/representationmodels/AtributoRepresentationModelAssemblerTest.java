package devs.mrp.gullproject.domains.representationmodels;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.domains.Tipo;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@EnableHypermediaSupport(type = { HypermediaType.HAL, HypermediaType.HAL_FORMS })
@SpringBootTest
@ContextConfiguration(classes = {AtributoModelTestConfig.class})
@ActiveProfiles("hackteoas")
class AtributoRepresentationModelAssemblerTest {
	
	// cuando haces @Mock para insertarlo en la instancia a testear
	//@InjectMocks
	//MaterialRepresentationModelAssembler mrma;
	
	// cuando no tiene dependencias
	@Autowired
	AtributoRepresentationModelAssembler arma;
	

	@Test
	void testToModel() {
		
		Tipo tipo = new Tipo();
		tipo.setDataFormat(DataFormat.DESCRIPCION);
		tipo.setNombre("type name");
		
		Atributo m = new Atributo();
		m.setName("size");
		m.setId("idaleatoria");
		m.setValoresFijos(true);
		//m.addTipo(tipo);
		m.setTipo(DataFormat.DESCRIPCION);
		Flux<Atributo> mFlux = Flux.just(m);
		
		AtributoRepresentationModel mrm = arma.toModel(m);
		
		assertThat(mrm.getId()).isSameAs(m.getId());
		assertThat(mrm.getName()).isSameAs(m.getName());
		assertThat(mrm.getTipo()).isSameAs(m.getTipo());
		assertThat(mrm.getLink("self").toString()).contains("/api/atributos/id/" + m.getId());

	}

}
                 