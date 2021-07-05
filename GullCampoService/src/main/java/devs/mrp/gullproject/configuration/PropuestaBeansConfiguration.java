package devs.mrp.gullproject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import devs.mrp.gullproject.service.propuesta.oferta.FromPropuestaToOfertaFactory;
import devs.mrp.gullproject.service.propuesta.oferta.FromPropuestaToOfertaFactoryImpl;
import devs.mrp.gullproject.service.propuesta.proveedor.ProposalCostNameMapperFromPvpFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.ProposalCostNameMapperFromPvpFactoryImpl;

@Configuration
public class PropuestaBeansConfiguration {

	@Bean
	public FromPropuestaToOfertaFactory toOfertaConverter() {
		return new FromPropuestaToOfertaFactoryImpl();
	}
	
	@Bean
	public ProposalCostNameMapperFromPvpFactory costMapper() {
		return new ProposalCostNameMapperFromPvpFactoryImpl();
	}
	
}
