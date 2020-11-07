package devs.mrp.gullproject.domains;

import java.util.Iterator;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.mongodb.lang.NonNull;

import lombok.Data;

@Data
@Document(collection = "materiales")
public class Material {

	@Id
	private String id;
	
	@NotBlank(message = "El nombre es obligatorio")
	private String name;
	
	private List<Material> hijos;
	
	public boolean isMaterial(Material m) {
		if (m.getId().equals(this.getId())) {
			return true;
		}
		
		Iterator<Material> i = hijos.iterator();
		while(i.hasNext()) {
			Material mat = i.next();
			if (mat.isMaterial(m)) {
				return true;
			}
		}
		
		return false;
	}
	
}
