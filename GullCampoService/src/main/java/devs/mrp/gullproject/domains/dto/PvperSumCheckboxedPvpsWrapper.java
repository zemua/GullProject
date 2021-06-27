package devs.mrp.gullproject.domains.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PvperSumCheckboxedPvpsWrapper {

	List<PvperSumCheckboxedPvps> sums;
	String name;
	String pvps;
	
}
