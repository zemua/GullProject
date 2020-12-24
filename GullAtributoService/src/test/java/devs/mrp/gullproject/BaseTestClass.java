package devs.mrp.gullproject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import devs.mrp.gullproject.rest.AtributoRestController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMessageVerifier
public class BaseTestClass {
	
	// https://www.baeldung.com/spring-cloud-contract

	@Autowired
	private AtributoRestController atributoRestController;
	
	@BeforeAll
    public void setup() {
        StandaloneMockMvcBuilder standaloneMockMvcBuilder 
          = MockMvcBuilders.standaloneSetup(atributoRestController);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);
    }
	
}
