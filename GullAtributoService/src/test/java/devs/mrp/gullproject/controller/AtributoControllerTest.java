package devs.mrp.gullproject.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.rest.AtributoRestController;
import devs.mrp.gullproject.rest.AtributoRestControllerById;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
//@SpringBootTest
@WebMvcTest(AtributoController.class)
@AutoConfigureMockMvc // se puede configurar sólo para clases específicas en BeforeAll
class AtributoControllerTest {
	
	// TODO completar todos los tests

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	AtributoService atributoService;
	
	@MockBean
	AtributoRestController arc;
	@MockBean
	AtributoRestControllerById arcbi;
	
	@BeforeEach
    public void setup() {
        //this.mockMvc = MockMvcBuilders.standaloneSetup(new AtributoController(atributoService)).build();
    }
	
	@Test
	void testCrearAtributo() throws Exception {
		
		/*Atributo a = new Atributo();
		a.setName("nombre");
		a.setTipo(DataFormat.COSTE);
		Flux<Atributo> flux = Flux.just(a);
		
		when(atributoService.findAll()).thenReturn(flux);*/
		
		mockMvc.perform(get("/atributos/nuevo"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Nombre:")))
			.andExpect(content().string(containsString("Tipo:")))
			.andExpect(content().string(containsString("Nuevo Atributo")))
			.andExpect(content().string(not(containsString("Guardado. ¿Añadir otro?"))));
		
		/*webTestClient.get()
			.uri("/atributos/nuevo")
			.accept(MediaType.APPLICATION_XHTML_XML)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(new BaseMatcher<String>() {

				@Override
				public boolean matches(Object actual) {
					String s = actual.toString();
					return s.contains("Nombre:")
							&& s.contains("Tipo:")
							&& s.contains("Nuevo Atributo");
				}

				@Override
				public void describeTo(Description description) {
				}
			});*/
	}
	
	@Test
	void testProcesarNuevoAtributo() throws Exception {
		Atributo a = new Atributo();
		a.setName("nombre");
		a.setTipo(DataFormat.CANTIDAD);
		a.setId("idDelMoNo");
		
		Atributo b = new Atributo();
		b.setName("nombre");
		b.setTipo(DataFormat.CANTIDAD);
		
		Mono<Atributo> mono = Mono.just(a);
		when(atributoService.save(ArgumentMatchers.eq(b))).thenReturn(mono);
		
		mockMvc.perform(post("/atributos/nuevo")
				.param("name", "nombre")
				.param("tipo", "CANTIDAD"))
		.andDo(print())
		.andExpect(status().is3xxRedirection());
		//.andExpect(content().string(containsString("Nombre:")))
		//.andExpect(content().string(containsString("Tipo:")))
		//.andExpect(content().string(containsString("Nuevo Atributo")))
		//.andExpect(content().string(containsString("Guardado. ¿Añadir otro?")));
		
		mockMvc.perform(get("/atributos/nuevo?add=1"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("Nombre:")))
		.andExpect(content().string(containsString("Tipo:")))
		.andExpect(content().string(containsString("Nuevo Atributo")))
		.andExpect(content().string(containsString("Guardado. ¿Añadir otro?")));
		
		mockMvc.perform(post("/atributos/nuevo")
				.param("name", "")
				.param("tipo", "CQWERCQ"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("Nombre:")))
		.andExpect(content().string(containsString("Tipo:")))
		.andExpect(content().string(containsString("Nuevo Atributo")))
		.andExpect(content().string(containsString("errores")))
		.andExpect(content().string(not(containsString("Guardado. ¿Añadir otro?"))));
	}
	
	@Test
	void testMostrarAtributos() {
		
	}

}
