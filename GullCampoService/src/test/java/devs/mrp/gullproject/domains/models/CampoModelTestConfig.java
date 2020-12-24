package devs.mrp.gullproject.domains.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.LineaService;

@Configuration
public class CampoModelTestConfig {
	
	/**
	 * necesita incluir dependencia mvc en el pom scope test
	 * que excluimos de la importación de hateoas porque
	 * da problemas, por el momento, con reactor
	 */
	
	@Bean
	public CampoRepresentationModelAssembler campoRepresentationModelAssembler() {
		return new CampoRepresentationModelAssembler();
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
