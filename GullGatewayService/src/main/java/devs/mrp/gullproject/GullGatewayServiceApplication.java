package devs.mrp.gullproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import devs.mrp.gullproject.configuration.MicroServiceConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(MicroServiceConfiguration.class)
@EnableEurekaClient
public class GullGatewayServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(GullGatewayServiceApplication.class, args);
	}
	
	// TODO hide services https://spring.io/blog/2019/07/01/hiding-services-runtime-discovery-with-spring-cloud-gateway
}