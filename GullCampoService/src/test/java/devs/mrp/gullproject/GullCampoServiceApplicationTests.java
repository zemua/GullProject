package devs.mrp.gullproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.LineaFactory;

@SpringBootTest
@Import({LineaFactory.class, Consulta.class})
class GullCampoServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
