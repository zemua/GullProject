package devs.mrp.gullproject.domains.models;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;

@ExtendWith(SpringExtension.class)
@EnableHypermediaSupport(type = { HypermediaType.HAL, HypermediaType.HAL_FORMS })
@SpringBootTest
//@ContextConfiguration(classes = {LineaRepresentationModelAssemblerTest.Config.class})
//@ActiveProfiles("hackteoas")
@Import({LineaFactory.class})
class LineaRepresentationModelAssemblerTest {
	
	/*static class Config {
		@Bean
		public LineaRepresentationModelAssembler lineaRepresentationModelAssembler() {
			return new LineaRepresentationModelAssembler();
		}
		
		@Bean
		public ServletUriComponentsBuilder servletUriComponentsBuilder() {
			MockHttpServletRequest request;
			
			request = new MockHttpServletRequest();
			request.setScheme("http");
			request.setServerName("localhost");
			request.setServerPort(-1);
			request.setRequestURI("/showcase");
			request.setContextPath("/showcase	");

			return ServletUriComponentsBuilder.fromRequest(request);
		}
	}

	@Autowired
	LineaRepresentationModelAssembler lrma;
	@Autowired
	LineaFactory lineaFactory;
	
	@Test
	void testToModel() {
		
		Campo<Integer> campo = new Campo<>();
		campo.setAtributoId("atributo_id");
		campo.setDatos(2345);
		campo.setId("campo_id");
		
		List<Campo<?>> campos = new ArrayList<>();
		campos.add(campo);
		
		Linea linea = lineaFactory.create();
		linea.setId("id_linea");
		linea.setNombre("nombre_linea");
		linea.setCampos(campos);
		
		LineaRepresentationModel lrm = lrma.toModel(linea);
		
		assertThat(lrm.getId()).isSameAs(linea.getId());
		assertThat(lrm.getCampos()).isSameAs(linea.getCampos());
		assertThat(lrm.getNombre()).isSameAs(linea.getNombre());
		assertThat(lrm.getLink("self").toString()).contains("/api/lineas/id/" + linea.getId());
	} */

}
