package devs.mrp.gullproject.domains.linea;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "campos")
@NoArgsConstructor
@AllArgsConstructor
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
	
	public Campo(Campo<T> c) {
		this.atributoId = c.atributoId;
		this.datos = c.datos;
	}
	
	public String getDatosText() {
		return String.valueOf(datos);
	}
	
	@SuppressWarnings("unchecked")
	public void setDatosCasting(Object d) {
		this.datos = (T)d;
	}

}
