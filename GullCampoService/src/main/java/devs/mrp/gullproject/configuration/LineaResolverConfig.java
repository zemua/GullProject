package devs.mrp.gullproject.configuration;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.gullproject.ainterfaces.MyFactoryNew;
import devs.mrp.gullproject.controller.LineaAttributeResolver;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;

/**
 * 
 * @author miguel
 *
 *	Currently deactivated as flux cannot work together bindingResult and custom ArgumentResolver
 *
 */
//@Configuration
public class LineaResolverConfig implements WebFluxConfigurer {
	
	private MyFactoryNew<Linea> factory;
	private ModelMapper mapper;
	
	@Autowired
	public LineaResolverConfig(LineaFactory factory, ModelMapper mapper) {
		this.factory = factory;
		this.mapper = mapper;
	}

	@Override
	public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
		configurer.addCustomResolver(new LineaAttributeResolver(factory, mapper));
	}
	
}
