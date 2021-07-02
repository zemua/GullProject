package devs.mrp.gullproject.domains.dto.propuesta.oferta;

import java.util.List;

import lombok.Data;

@Data
public class PvperCheckbox {

	String id;
	List<String> idCostes;
	String name;
	boolean selected;
	
}
