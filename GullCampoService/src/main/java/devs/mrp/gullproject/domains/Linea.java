package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Document (collection = "lineas")
public class Linea {

	@Id
	private String id;
	
	@NotBlank
	private String nombre;
	
	private Integer orden;
	
	private List<Campo<?>> campos = new ArrayList<>();
	//private Flux<Campo<?>> campos;
	
	@JsonIgnore
	public int getCantidadCampos() {
		return campos.size();
	}
	
	public Campo<?> getCampo(int i){
		return campos.get(i);
	}
	
}
