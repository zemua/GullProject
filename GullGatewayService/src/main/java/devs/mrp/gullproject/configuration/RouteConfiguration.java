package devs.mrp.gullproject.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

	RouteLocatorBuilder mBuilder;
	MicroServiceConfiguration micro;
	
	@Autowired
	private TokenRelayGatewayFilterFactory filterFactory; // para OAuth2
	
	@Autowired
	public RouteConfiguration(RouteLocatorBuilder mBuilder, MicroServiceConfiguration micro) {
		this.mBuilder = mBuilder;
		this.micro = micro;
	}
	
	@Bean
	  public RouteLocator myRoutes(RouteLocatorBuilder builder) {
	    return builder.routes()
	    		
	    	/*.route(r -> r.path("/users/**")
	    			.filters(f -> f.filters(filterFactory.apply())
	    					//.removeRequestHeader("Cookie")
	    					)
	    			.uri(micro.getAuthService()))
	    	.route(r -> r.path("/login/**")
	    			.filters(f -> f.filters(filterFactory.apply())
	    					//.removeRequestHeader("Cookie")
	    					)
	    			.uri(micro.getAuthService()))*/
	    	
	      .route(r -> r.path("/consultas/**")
	    		  .filters(f -> f.filters(filterFactory.apply())
	    	                .removeRequestHeader("Cookie"))
	    		  .uri(micro.getCampoService()))
	      .route(r -> r.path("/lineas/**")
	    		  .filters(f -> f.filters(filterFactory.apply())
	    	                .removeRequestHeader("Cookie"))
	    		  .uri(micro.getCampoService()))
	      
	      .route(r -> r.path("/atributos/**")
	    		  .filters(f -> f.filters(filterFactory.apply())
	    	                .removeRequestHeader("Cookie"))
	    		  .uri(micro.getAtributoService()))
	      
	      .route(r -> r.path("/css/**")
	    		  .filters(f -> f.filters(filterFactory.apply())
	    	                .removeRequestHeader("Cookie"))
	    		  .uri(micro.getCampoService())) // los primeros archivos CSS, para otros, poner en carpeta con nombre de modulo
	      .route(r -> r.path("/js/**")
	    		  .filters(f -> f.filters(filterFactory.apply())
	    	                .removeRequestHeader("Cookie"))
	    		  .uri(micro.getCampoService())) // los primeros archivos JS, para otros, poner en carpeta con nombre de módulo
	      
	      .build();
	  }
	
}
