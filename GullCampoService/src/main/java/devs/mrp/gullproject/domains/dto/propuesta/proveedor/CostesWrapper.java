package devs.mrp.gullproject.domains.dto.propuesta.proveedor;

import java.util.List;

import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostesWrapper {

	List<CosteProveedor> costes;
	
}
