package devs.mrp.gullproject.domains.dto.propuesta.proveedor;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostesOrdenablesWrapper {

	List<CosteOrdenable> costes;
	
}
