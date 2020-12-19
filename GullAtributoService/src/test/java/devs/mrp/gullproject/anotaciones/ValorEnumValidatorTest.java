package devs.mrp.gullproject.anotaciones;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import devs.mrp.gullproject.repositorios.AtributoRepo;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ValorEnumValidatorTest {
	
	// TODO https://www.baeldung.com/spring-mvc-custom-validator#tests
	// cuando esté hecha la plantilla de thymeleaf rulará, mientras no
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	AtributoRepo atributoRepo;
	
	@Test
	public void givenCrearAtributoUri_whenMockMvc_thenReturnsAtributoPage() throws Exception {
			mockMvc.perform(get("/atributos/nuevo")).andExpect(view().name("crearAtributo"));
	}
	
	@Test
	public void givenAtributoUriPostWithPostAndFormatData_whenMockMvc_ThenVerifyErrorResponse() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/atributos/nuevo").
			      accept(MediaType.TEXT_HTML).
			      param("phoneInput", "123")).
			      andExpect(model().attributeHasFieldErrorCode(
			          "atributo","tipo","ValorEnum")).
			      andExpect(view().name("phoneHome")).
			      andExpect(status().isOk()).
			      andDo(print());
	}
	


}
