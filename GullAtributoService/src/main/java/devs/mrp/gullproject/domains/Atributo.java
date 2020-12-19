package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import devs.mrp.gullproject.anotaciones.ValorEnum;
import lombok.Data;

@Data
@Document(collection = "atributos")
public class Atributo {
	
	@Id
	private String id;
	
	@NotBlank(message = "El nombre es obligatorio")
	private String name;
	
	@NotNull
	@ValorEnum(enumClass = DataFormat.class, message = "Escoge un tipo correcto")
	private DataFormat tipo;
	
	/**
	 * flag para saber si los valores
	 * se deben escoger de un dropdown
	 * o si deben ser escritos por el usuario
	 */
	private boolean valoresFijos;
	
	List<String> valoresPredefinidos = new ArrayList<>();

}
