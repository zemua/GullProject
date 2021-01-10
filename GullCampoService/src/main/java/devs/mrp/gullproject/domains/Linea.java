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
	
	/**
	 * When creating an updated snapshot, this will refer to the "old" line
	 */
	private String parentId;
	/**
	 * When creating an offer of suppliers or ours, this will refer to the customer line
	 */
	private String counterLineId;
	
	private Integer order;
	
	private List<Campo<?>> campos = new ArrayList<>();
	
	@JsonIgnore
	public int getCantidadCampos() {
		return campos.size();
	}
	
	public Campo<?> getCampoByIndex(int i){
		return campos.get(i);
	}
	
}
