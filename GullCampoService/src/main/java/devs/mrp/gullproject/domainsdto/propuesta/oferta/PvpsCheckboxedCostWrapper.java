package devs.mrp.gullproject.domainsdto.propuesta.oferta;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PvpsCheckboxedCostWrapper {

	List<PvperCheckboxedCosts> pvps;
	String name;
	String costes;
	
}
