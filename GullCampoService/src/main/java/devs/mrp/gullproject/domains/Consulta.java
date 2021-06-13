package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import devs.mrp.gullproject.service.ConsultaOperations;
import lombok.Data;

@Data
@Document(collection = "consultas")
public class Consulta {
	
	@Id
	String id  = new ObjectId().toString();
	
	@NotBlank(message = "El nombre es obligatorio.")
	String nombre;
	@NotBlank(message = "El estado es obligatorio.")
	String status;
	
	Long createdTime = System.currentTimeMillis();
	Long editedTime;
	
	List<Propuesta> propuestas = new ArrayList<>();
	
	public ConsultaOperations operations() {
		return new ConsultaOperations(this);
	}
	
}
