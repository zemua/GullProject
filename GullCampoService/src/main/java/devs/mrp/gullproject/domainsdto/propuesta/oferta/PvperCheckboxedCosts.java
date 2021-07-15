package devs.mrp.gullproject.domainsdto.propuesta.oferta;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PvperCheckboxedCosts {
	
	String id;
	List<CheckboxedCostId> costs;
	Map<String, List<CheckboxedAttId>> attributesByCost;
	String name;

	@Data
	@NoArgsConstructor
	public static class CheckboxedCostId {
		String id;
		boolean selected;
	}
	
	@Data
	@NoArgsConstructor
	public static class CheckboxedAttId {
		String id;
		boolean selected;
	}
	
}
