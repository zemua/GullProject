package devs.mrp.gullproject.domains.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PvperSumCheckboxWrapper {

	List<PvperSumCheckbox> sums;
	
}
