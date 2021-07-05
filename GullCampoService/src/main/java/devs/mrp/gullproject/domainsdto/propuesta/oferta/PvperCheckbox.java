package devs.mrp.gullproject.domainsdto.propuesta.oferta;

import java.util.List;

import lombok.Data;

@Data
public class PvperCheckbox {

	String id;
	List<String> idCostes;
	String name;
	boolean selected;
	
}
