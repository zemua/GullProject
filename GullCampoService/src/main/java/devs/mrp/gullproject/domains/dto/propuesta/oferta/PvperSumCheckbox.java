package devs.mrp.gullproject.domains.dto.propuesta.oferta;

import java.util.List;

import lombok.Data;

@Data
public class PvperSumCheckbox {

	String id;
	List<String> pvperIds;
	String name;
	boolean selected;
	
}
