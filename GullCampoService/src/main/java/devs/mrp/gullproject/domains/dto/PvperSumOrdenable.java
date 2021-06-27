package devs.mrp.gullproject.domains.dto;

import java.util.List;

import lombok.Data;

@Data
public class PvperSumOrdenable {

	String id;
	List<String> pvperIds;
	String name;
	int order;
	
}
