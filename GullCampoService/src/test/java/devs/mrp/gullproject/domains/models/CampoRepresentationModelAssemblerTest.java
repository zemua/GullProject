package devs.mrp.gullproject.domains.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Campo;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@EnableHypermediaSupport(type = { HypermediaType.HAL, HypermediaType.HAL_FORMS })
@SpringBootTest
@ContextConfiguration(classes = {CampoModelTestConfig.class})
class CampoRepresentationModelAssemblerTest {
	
	@Autowired
	CampoRepresentationModelAssembler crma;

	@Test
	void testToModel() {
		
		Campo<Integer> campo = new Campo<>();
		campo.setAtributoId("atributo_id");
		campo.setDatos(2345);
		campo.setId("campo_id");
		
		CampoRepresentationModel crm = crma.toModel(campo);
		
		assertThat(crm.getId()).isSameAs(campo.getId());
		assertThat(crm.getAtributoId()).isSameAs(campo.getAtributoId());
		assertThat(crm.getDatos()).isSameAs(campo.getDatos());
		assertThat(crm.getLink("self").toString()).contains("/api/campos/id/" + campo.getId());
		
	}

}
