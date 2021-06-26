package devs.mrp.gullproject.domains.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PvperCheckboxedCosts {
	
	String id;
	List<CheckboxedCostId> costs;
	String name;

	@Data
	@NoArgsConstructor
	public static class CheckboxedCostId {
		String id;
		boolean selected;
	}
	
}
