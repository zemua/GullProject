package devs.mrp.gullproject.domains;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.Data;

@Data
@Document
public class Tipo {

	@Id
	private String id;
	
	@NonNull
	private String name;
	
	private Date createdAt = new Date();
	
}
