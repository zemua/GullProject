package devs.mrp.gullproject.domains.dto.propuesta.oferta;

import java.util.List;

import lombok.Data;

@Data
public class PvperSumOrdenable {

	String id;
	List<String> pvperIds;
	String name;
	int order;
	
}
