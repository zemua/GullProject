package devs.mrp.gulleureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class GullEurekaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GullEurekaServiceApplication.class, args);
	}

}
