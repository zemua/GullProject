package devs.mrp.gullproject.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

	/*@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}*/
	
	@Bean
	@LoadBalanced
	public WebClient.Builder webClient(ReactiveClientRegistrationRepository clientRegistrations,
	        ServerOAuth2AuthorizedClientRepository authorizedClients) {
	    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
	            new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
	    // (optional) explicitly opt into using the oauth2Login to provide an access token implicitly
	    // oauth.setDefaultOAuth2AuthorizedClient(true); // use existing authenticated token from a logged-in user
	    // (optional) set a default ClientRegistration.registrationId
	    oauth.setDefaultClientRegistrationId("gull-campo-service"); // use this server's own client credentials to authenticate
	    return WebClient.builder()
	            .filter(oauth);
	            //.build();
	}
	
}
