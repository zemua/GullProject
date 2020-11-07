package devs.mrp.gullproject.domains;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.mongodb.lang.NonNull;

import lombok.Data;

@Data
@Document
public class Pieza {

	@Id
	private String id;
	
	@NonNull
	private Parte parte;
	@NonNull
	private Material material;
	
}
