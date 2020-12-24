package devs.mrp.gullproject.domains.representationmodels;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Configuration
@SpringBootTest
@ActiveProfiles("hackteoas")
public class AtributoModelTestConfig {
	
	/**
	 * necesita incluir dependencia mvc en el pom scope test
	 * que excluimos de la importación de hateoas porque
	 * da problemas, por el momento, con reactor
	 */

	@Bean
	public AtributoRepresentationModelAssembler atributoRepresentationModelAssembler() {
		return new AtributoRepresentationModelAssembler();
	}
	
	/*@Bean
	public ServletUriComponentsBuilder servletUriComponentsBuilder() {
		MockHttpServletRequest request;
		
		request = new MockHttpServletRequest();
		request.setScheme("http");
		request.setServerName("localhost");
		request.setServerPort(-1);
		request.setRequestURI("/showcase");
		request.setContextPath("/showcase	");

		return ServletUriComponentsBuilder.fromRequest(request);
	}*/
	
}
