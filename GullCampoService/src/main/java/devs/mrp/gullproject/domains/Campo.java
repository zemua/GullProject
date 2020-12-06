package devs.mrp.gullproject.domains;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "campos")
public class Campo<T> {
	
	@Id
	private String id;
	
	@NotBlank
	private String atributoId;
	
	private T datos;

}
