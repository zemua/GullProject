package devs.mrp.gullproject.domainsdto.propuesta.oferta;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PvperCheckboxedCosts {
	
	String id;
	List<CheckboxedCostId> costs;
	List<AttsList> attributesByCotiz;
	@NotBlank(message = "debes escoger un nombre")
	String name;

	@Data
	@NoArgsConstructor
	public static class CheckboxedCostId {
		String id;
		boolean selected;
	}
	
	@Data
	@NoArgsConstructor
	public static class AttsList {
		String cotizId;
		List<CheckboxedAttId> atts;
	}
	
	@Data
	@NoArgsConstructor
	public static class CheckboxedAttId {
		String id;
		boolean selected;
	}
	
}
