package devs.mrp.gullproject.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties(prefix = "gullclient")
@Data
public class ClientProperties {

	String atributoServiceUrl;
	String atributoServiceHost;
	
	public ClientProperties(){
		this.atributoServiceUrl = "http://atributo-service/";
		this.atributoServiceHost = "atributo-service";
	}
	
}
