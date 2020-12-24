package devs.mrp.gullproject.domains.models;

import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class LineaModelTestConfig {

	/**
	 * necesita incluir dependencia mvc en el pom scope test
	 * que excluimos de la importación de hateoas porque
	 * da problemas, por el momento, con reactor
	 */
	
	@Bean
	public LineaRepresentationModelAssembler lineaRepresentationModelAssembler() {
		return new LineaRepresentationModelAssembler();
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
