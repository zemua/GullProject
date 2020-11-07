package devs.mrp.gullproject.domains;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.mongodb.lang.NonNull;

import lombok.Data;

@Data
@Document
public class Valvula {

	@Id
	private String id;
	
	@NonNull
	private Tipo tipo;
	
	private int size;
	private int rating;
	
	private List<Pieza> piezas;
	
	private Date createdAt = new Date();
	
}
