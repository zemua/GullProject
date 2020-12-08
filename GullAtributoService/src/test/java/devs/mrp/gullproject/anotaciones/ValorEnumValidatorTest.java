package devs.mrp.gullproject.anotaciones;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import devs.mrp.gullproject.controller.AtributoController;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class ValorEnumValidatorTest {
	
	// TODO punto 9.4 https://www.baeldung.com/spring-mvc-custom-validator#tests
	
	@Autowired
	MockMvc mockMvc;
	
	


}
