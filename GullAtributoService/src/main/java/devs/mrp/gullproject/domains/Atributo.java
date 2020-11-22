package devs.mrp.gullproject.domains;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "atributos")
public class Atributo {
	
	@Id
	private String id;
	
	@NotBlank(message = "El nombre es obligatorio")
	private String name;
	
	private Map<String, dataFormat> tipos;
	
	private boolean valoresFijos;
	
	public enum dataFormat {
		atributoTexto, cantidad, coste, margen, pvp, plazo;
	}

}
