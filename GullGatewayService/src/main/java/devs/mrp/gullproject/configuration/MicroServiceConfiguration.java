package devs.mrp.gullproject.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class MicroServiceConfiguration {
	private String atributoService = "lb://ATRIBUTO-SERVICE";
	private String campoService = "lb://CAMPO-SERVICE";
	
	public String getAtributoService() {
		return atributoService;
	}
	
	public void setAtributoService(String s) {
		atributoService = s;
	}
	
	public String getCampoService() {
		return campoService;
	}
	
	public void setCampoService(String s) {
		campoService = s;
	}
}
