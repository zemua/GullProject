package devs.mrp.gullproject.domains;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class CosteProveedor {

	String id = new ObjectId().toString();
	String name;
	
}
