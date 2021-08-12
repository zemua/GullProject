package devs.mrp.gullproject.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class ResourceServerSecurityConfig {

	@Value("${jwk.endpoint}")
	private String jwkEndpoint;
	
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) { // TODO test security
		return http
				.csrf().disable()
				.cors(c -> {
					CorsConfigurationSource source = request -> {
						CorsConfiguration config = new CorsConfiguration();
						config.setAllowedOrigins(List.of("*"));
						config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
						return config;
					};
					c.configurationSource(source);
				})
				.authorizeExchange()
				.anyExchange()
					.authenticated()
				.and().oauth2ResourceServer()
					.jwt(customizer -> {
						customizer.jwkSetUri(jwkEndpoint);
					})
				.and()
				//.oauth2Client(Customizer.withDefaults())
				.build();
	}
	
	@Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.addAllowedMethod(HttpMethod.PUT);
        corsConfig.addAllowedMethod(HttpMethod.DELETE);
        corsConfig.setAllowedOrigins(Arrays.asList("file:///home/"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
	
}
