package devs.mrp.gullproject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import devs.mrp.gullproject.domains.representationmodels.representationmodelassemblersupport.MaterialRepresentationModelAssembler;

@Configuration
public class ValveServiceTestConfig {

	@Bean
	public MaterialRepresentationModelAssembler materialRepresentationModelAssembler() {
		return new MaterialRepresentationModelAssembler();
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
