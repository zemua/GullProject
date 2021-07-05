package devs.mrp.gullproject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import devs.mrp.gullproject.service.propuesta.oferta.FromPropuestaToOfertaFactory;
import devs.mrp.gullproject.service.propuesta.oferta.FromPropuestaToOfertaFactoryImpl;

@Configuration
public class PropuestaBeansConfiguration {

	@Bean
	public FromPropuestaToOfertaFactory toOfertaConverter() {
		return new FromPropuestaToOfertaFactoryImpl();
	}
	
}
