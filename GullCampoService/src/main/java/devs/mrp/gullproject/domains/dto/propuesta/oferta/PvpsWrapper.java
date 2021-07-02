package devs.mrp.gullproject.domains.dto.propuesta.oferta;

import java.util.List;

import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PvpsWrapper {

	List<Pvper> pvps;
	
}
