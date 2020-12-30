package devs.mrp.gullproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableHypermediaSupport(type = { HypermediaType.HAL, HypermediaType.HAL_FORMS })
@EnableDiscoveryClient
//@EnableReactiveFeignClients // rompe otros tests, añadimos esto en una configuracion específica del reactive feign para que no se cargue automático con springboottest
public class GullCampoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GullCampoServiceApplication.class, args);
	}

}
