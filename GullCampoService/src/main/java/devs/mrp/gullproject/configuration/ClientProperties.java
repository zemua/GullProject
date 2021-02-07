package devs.mrp.gullproject.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties(prefix = "gullclient")
@Data
public class ClientProperties {

	String atributoServiceUrl;
	
	public ClientProperties(){
		this.atributoServiceUrl = "/atributo-service/";
	}
	
}
