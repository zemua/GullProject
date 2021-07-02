package devs.mrp.gullproject.domains.dto.propuesta.oferta;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PvperSumCheckboxedPvps {

	String id;
	List<CheckboxedPvperId> pvperIds;
	String name;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CheckboxedPvperId {
		String id;
		boolean selected;
	}
	
}
