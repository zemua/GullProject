package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
	
	public int getPropuestaIndexByPropuestaId(String id) {
		for(int i = 0; i<propuestas.size(); i++) {
			if (propuestas.get(i).getId().equals(id)) {
				return i;
			}
		}
		return -1;
	}
	
	public Propuesta getPropuestaById(String id) {
		Iterator<Propuesta> i = propuestas.iterator();
		while (i.hasNext()) {
			Propuesta p = i.next();
			if (p.getId().equals(id)) {
				return p;
			}
		}
		return null;
	}
	
}
