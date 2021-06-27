package devs.mrp.gullproject.domains.dto;

import java.util.List;

import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.Pvper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PvpsWrapper {

	List<Pvper> pvps;
	
}
