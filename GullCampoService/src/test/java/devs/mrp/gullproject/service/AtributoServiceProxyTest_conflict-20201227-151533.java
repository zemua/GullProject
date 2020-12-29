package devs.mrp.gullproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow.Publisher;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
//import org.springframework.cloud.netflix.ribbon.RibbonClient;
//import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import devs.mrp.gullproject.configuration.FeignResponseDecodeConfig;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import reactor.core.publisher.Flux;

//import com.netflix.loadbalancer.Server;
//import com.netflix.loadbalancer.ServerList;

import reactor.core.publisher.Mono;

@SpringBootTest(classes = {
	      //AtributoServiceProxyTest.FakeFeignConfiguration.class,
	      AtributoServiceProxyTest.FakeRestService.class
	    },
		webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(FeignResponseDecodeConfig.class)
class AtributoServiceProxyTest {
	
	// https://codeburst.io/unit-testing-feignclient-using-restcontroller-and-ribbonclient-4e76bfeddf41
	// https://github.com/getstarted-spring/feignclient-unit-test
	
	// https://medium.com/swlh/spring-boot-how-to-unit-test-a-feign-client-in-isolation-using-only-service-name-4e0fc9e151a7
	
	// y load balancer
	// https://spring.io/guides/gs/spring-cloud-loadbalancer/
	// https://spring.io/blog/2020/03/25/spring-tips-spring-cloud-loadbalancer
	
	// TODO
	
	/**
	 * Simulación de la API
	 */
	
	@RestController
	@RequestMapping(path = "/api/atributos/data-validator")
	static class FakeRestService {
		
		@GetMapping(params = {"type", "data"}, produces = "application/json")
		public String getMessage(@RequestParam("type") final String type,
								@RequestParam("data") final String data) {

			assertThat(type)
			.isEqualTo("CANTIDAD");
			assertThat(data)
			.isEqualTo("12345");

			return "true";
		}
	}
	
	/*@Configuration(proxyBeanMethods = false)
	static class FakeRibbonConfiguration {
		@LocalServerPort int port;    
		@Bean public ServerList<Server> serverList() {
			return new StaticServerList<>(new Server("localhost", port));
		}
	}*/
	
	/*@Configuration(proxyBeanMethods = false)
	@EnableReactiveFeignClients(clients = AtributoServiceProxy.class)			
	@EnableAutoConfiguration
	//@RibbonClient(name = "atributo-service", configuration = AtributoServiceProxyTest.FakeRibbonConfiguration.class)
	//@LoadBalancerClient(name = "atributo-service", configuration = AtributoServiceProxyTest.SayHelloConfiguration.class)
	@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
		  static class FakeFeignConfiguration {}*/
	
	/**
	 * A continuación sale de
	 * https://spring.io/guides/gs/spring-cloud-loadbalancer/
	 */
	
	/*@Configuration
	static class SayHelloConfiguration {

	  @Bean
	  @Primary
	  ServiceInstanceListSupplier serviceInstanceListSupplier() {
	    return new ServiceInstanceListSupplier() {
	    	
	    	@LocalServerPort int port;

			@Override
			public Flux<List<ServiceInstance>> get() {
				return Flux.just(Arrays
				        .asList(new DefaultServiceInstance(getServiceId() + "1", getServiceId(), "localhost", port, false)));
			}

			@Override
			public String getServiceId() {
				return "atributo-service";
			}
	    	
	    };
	  }*/
	  
	  /*@Bean
	  @LoadBalanced
	  WebClient.Builder builder() {
		  return WebClient.builder();
	  }

	  @Bean
	  WebClient webClient(WebClient.Builder builder) {
		  return builder.build();
	  }*/

	}
	
	
	/**
	 * Aquí empieza el test
	 */
	
	@Autowired
	AtributoServiceProxy atributoServiceProxy;

	@Test
	void testValidateDataFormat() {		
		 assertThat(this.atributoServiceProxy.validateDataFormat(Mono.just("CANTIDAD"), Mono.just("12345")).block())
		 	.asString()
		 	.isEqualTo("true");
	}

}
