package devs.mrp.gullproject.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.repository.CampoRepo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CampoServiceTest {
	
	// TODO
	
	@MockBean
	AtributoServiceProxy asp;
	@MockBean
	CampoRepo campoRepo;

	@Test
	void testValidateDataFormat() {
		fail("Not yet implemented");
	}

}
