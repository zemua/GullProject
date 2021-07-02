package devs.mrp.gullproject.domains.dto.propuesta.proveedor;

import java.util.List;

import javax.validation.Valid;

import devs.mrp.gullproject.domains.dto.propuesta.AtributoForFormDto;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WrapPropuestaProveedorAndSelectableAttributes {

	@Valid PropuestaProveedor propuestaProveedor;
	List<AtributoForFormDto> attributes;
	
}
