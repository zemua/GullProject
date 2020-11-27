package devs.mrp.gullproject.domains;

import java.util.List;
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
	
	private List<Tipo> tipos;
	
	/**
	 * flag para saber si los valores
	 * se deben escoger de un dropdown
	 * o si deben ser escritos por el usuario
	 */
	private boolean valoresFijos;

}
