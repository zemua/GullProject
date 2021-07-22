package devs.mrp.gullproject.rest;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.hateoas.Link;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModel;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModelAssembler;
import devs.mrp.gullproject.service.AtributoService;
import io.restassured.RestAssured;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@DirtiesContext
@AutoConfigureMessageVerifier
public abstract class AtributosBase {

	@LocalServerPort int port;
	
	@MockBean
	AtributoService atributoService;
	@MockBean
	AtributoRepresentationModelAssembler arma;
	
	Atributo atributo;
	Atributo atributo2;
	Mono<Atributo> mAtributo;
	Flux<Atributo> fAtributo;
	Flux<Atributo> idAtributo;
	AtributoRepresentationModel atributoModel;
	AtributoRepresentationModel atributoModel2;
	
	@BeforeEach
	public void init() {
		atributo = new Atributo();
		atributo.setId("unID123");
		atributo.setName("name Atributo");
		atributo.setTipo(DataFormat.DESCRIPCION);
		atributo.setValoresFijos(false);
		mAtributo = Mono.just(atributo);
		when(atributoService.findById(ArgumentMatchers.eq(atributo.getId()))).thenReturn(mAtributo);
		atributoModel = new AtributoRepresentationModel();
		atributoModel.setId(atributo.getId());
		atributoModel.setName(atributo.getName());
		atributoModel.setTipo(atributo.getTipo());
		atributoModel.setValoresFijos(atributo.isValoresFijos());
		atributoModel.add(Link.of("/api/esto/es/un/link"));
		when(arma.toModel(atributo)).thenReturn(atributoModel);
		
		atributo2 = new Atributo();
		atributo2.setId("unID321");
		atributo2.setName("name Atributo dos");
		atributo2.setTipo(DataFormat.DECIMAL);
		atributo.setValoresFijos(true);
		
		fAtributo = Flux.just(atributo, atributo2);
		when(atributoService.findAll()).thenReturn(fAtributo);
		atributoModel2 = new AtributoRepresentationModel();
		atributoModel2.setId(atributo2.getId());
		atributoModel2.setName(atributo2.getName());
		atributoModel2.setTipo(atributo2.getTipo());
		atributoModel2.setValoresFijos(atributo2.isValoresFijos());
		atributoModel2.add(Link.of("/api/otro/link"));
		when(arma.toModel(atributo2)).thenReturn(atributoModel2);
		
		
		idAtributo = Flux.just(atributo, atributo2);
		when(atributoService.findAtributoByIdIn(ArgumentMatchers.anyList())).thenReturn(idAtributo);
	}
	
	@BeforeEach
	public void setup() {
		// This doesn't work with WebFlux, is for MockMvc
		/*StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(atributoRestController);
		RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);*/
		
		//This is for WebFlux and WebTestClient
		RestAssured.baseURI = "http://localhost:" + this.port;
	}
	
}
