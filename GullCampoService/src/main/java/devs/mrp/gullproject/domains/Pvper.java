package devs.mrp.gullproject.domains;

import java.util.List;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class Pvper {

	String id = new ObjectId().toString();
	List<String> idCostes;
	String name;
	
}
