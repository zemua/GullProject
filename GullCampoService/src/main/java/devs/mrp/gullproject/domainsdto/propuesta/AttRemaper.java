package devs.mrp.gullproject.domainsdto.propuesta;

import lombok.Data;

@Data
public class AttRemaper {

	String localIdentifier;
	String atributoId;
	String name;
	String tipo;
	String before;
	String after;
	
	String clase;
	Object afterObj;
	
}
