package devs.mrp.gullproject.domains.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;

@ExtendWith(SpringExtension.class)
@EnableHypermediaSupport(type = { HypermediaType.HAL, HypermediaType.HAL_FORMS })
@SpringBootTest
@ContextConfiguration(classes = {LineaModelTestConfig.class})
class LineaRepresentationModelAssemblerTest {

	@Autowired
	LineaRepresentationModelAssembler lrma;
	
	@Test
	void testToModel() {
		
		Campo<Integer> campo = new Campo<>();
		campo.setAtributoId("atributo_id");
		campo.setDatos(2345);
		campo.setId("campo_id");
		
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(campo);
		
		Linea linea = new Linea();
		linea.setId("id_linea");
		linea.setNombre("nombre_linea");
		linea.setCampos(campos);
		
		LineaRepresentationModel lrm = lrma.toModel(linea);
		
		assertThat(lrm.getId()).isSameAs(linea.getId());
		assertThat(lrm.getCampos()).isSameAs(linea.getCampos());
		assertThat(lrm.getNombre()).isSameAs(linea.getNombre());
		assertThat(lrm.getLink("self").toString()).contains("/api/lineas/id/" + linea.getId());
	}

}
