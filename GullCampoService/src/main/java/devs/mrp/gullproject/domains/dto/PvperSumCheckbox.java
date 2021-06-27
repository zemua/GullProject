package devs.mrp.gullproject.domains.dto;

import java.util.List;

import lombok.Data;

@Data
public class PvperSumCheckbox {

	String id;
	List<String> pvperIds;
	String name;
	boolean selected;
	
}
