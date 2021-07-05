package devs.mrp.gullproject.domainsdto.propuesta.oferta;

import java.util.List;

import lombok.Data;

@Data
public class PvperSumCheckbox {

	String id;
	List<String> pvperIds;
	String name;
	boolean selected;
	
}
