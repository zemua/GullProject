package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "consultas")
public class Consulta {
	
	@Id
	String id;
	
	@NotBlank(message = "El nombre es obligatorio.")
	String nombre;
	@NotBlank(message = "El estado es obligatorio.")
	String status;
	
	Long createdTime = System.currentTimeMillis();
	Long editedTime;
	
	List<Propuesta> propuestas = new ArrayList<>();
	
	public void actualizaEditTime() {
		editedTime = System.currentTimeMillis();
	}
	
	public void addPropuesta(Propuesta propuesta) {
		propuestas.add(propuesta);
	}
	
	public void removePropuesta(Propuesta propuesta) {
		propuestas.remove(propuesta);
		actualizaEditTime();
	}
	
	public void removePropuesta(String propuestaId) {
		propuestas.removeIf(propuesta -> propuesta.getId().equals(propuestaId));
	}
	
	public int getCantidadPropuestas() {
		return propuestas.size();
	}
	
	public Propuesta getPropuestaByIndex(int index) {
		return propuestas.get(index);
	}
	
}
