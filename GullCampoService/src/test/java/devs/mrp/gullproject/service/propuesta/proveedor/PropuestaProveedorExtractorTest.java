package devs.mrp.gullproject.service.propuesta.proveedor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Import({Consulta.class})
class PropuestaProveedorExtractorTest {

	@Autowired PropuestaProveedorExtractor extractor;
	
	@Test
	void test() {
		Propuesta p1 = new PropuestaCliente();
		Propuesta p2 = new PropuestaProveedor();
		
		var flux = extractor.filter(Flux.just(p1, p2));
		StepVerifier.create(flux)
		.assertNext(p -> {
			assertEquals(p2.getId(), p.getId());
		})
		.expectComplete()
		.verify()
		;
	}

}
