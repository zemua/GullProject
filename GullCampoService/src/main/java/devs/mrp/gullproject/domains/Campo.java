package devs.mrp.gullproject.domains;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "campos")
public class Campo<T> {
	
	@Id
	private String id = new ObjectId().toString();
	
	@NotBlank
	private String atributoId;
	
	private T datos;
	
	public Campo(String atributoId, T datos) {
		this.atributoId = atributoId;
		this.datos = datos;
	}
	
	public Campo() {}
	
	public String getDatosText() {
		return String.valueOf(datos);
	}

}
