package devs.mrp.gullproject.domains;

import java.util.ArrayList;
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
	
	private List<Tipo> tipos = new ArrayList<>();
	
	/**
	 * flag para saber si los valores
	 * se deben escoger de un dropdown
	 * o si deben ser escritos por el usuario
	 */
	private boolean valoresFijos;
	
	public void addTipo(Tipo t) {
		tipos.add(t);
	}
	
	public Tipo getTipo(int i) {
		return tipos.get(i);
	}
	
	public boolean removeTipo(Tipo t) {
		return tipos.remove(t);
	}
	
	public int getCantidadTipos() {
		return tipos.size();
	}

}
