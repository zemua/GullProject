package devs.mrp.gullproject;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMessageVerifier
public class BaseTestClass {
	
	// https://www.baeldung.com/spring-cloud-contract

	/*@Autowired
	private AtributoRestController atributoRestController;
	
	@BeforeAll
    public void setup() {
        StandaloneMockMvcBuilder standaloneMockMvcBuilder 
          = MockMvcBuilders.standaloneSetup(atributoRestController);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);
    }*/
	
}
