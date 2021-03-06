package devs.mrp.gullproject.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

	@Value("${jwk.endpoint}")
	private String jwkEndpoint;
	
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
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
				//.pathMatchers(HttpMethod.GET, "/hello")
				.anyExchange()
					//.hasRole(role)
					.authenticated()
				.and().oauth2ResourceServer()
					.jwt(jwtSpec -> {
						jwtSpec.jwkSetUri(jwkEndpoint);
					})
				.and().build();
	}
	
}
