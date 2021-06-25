package devs.mrp.gullproject.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

	RouteLocatorBuilder mBuilder;
	MicroServiceConfiguration micro;
	
	@Autowired
	public RouteConfiguration(RouteLocatorBuilder mBuilder, MicroServiceConfiguration micro) {
		this.mBuilder = mBuilder;
		this.micro = micro;
	}
	
	@Bean
	  public RouteLocator myRoutes(RouteLocatorBuilder builder) {
	    return builder.routes()
	      .route(r -> r.path("/consultas/**")
	    		  .uri(micro.getCampoService()))
	      .route(r -> r.path("/lineas/**")
	    		  .uri(micro.getCampoService()))
	      .route(r -> r.path("/atributos/**")
	    		  .uri(micro.getAtributoService()))
	      .route(r -> r.path("/css/**")
	    		  .uri(micro.getCampoService())) // los primeros archivos CSS, para otros, poner en carpeta con nombre de modulo
	      .route(r -> r.path("/js/**")
	    		  .uri(micro.getCampoService())) // los primeros archivos JS, para otros, poner en carpeta con nombre de módulo
	      .build();
	  }
	
}
