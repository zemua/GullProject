package devs.mrp.gullproject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.StringWrapper;
import devs.mrp.gullproject.service.AtributoServiceProxy;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveFeignClients(clients = {AtributoServiceProxy.class})
public class ReactiveFeignConfiguration {
	
}
