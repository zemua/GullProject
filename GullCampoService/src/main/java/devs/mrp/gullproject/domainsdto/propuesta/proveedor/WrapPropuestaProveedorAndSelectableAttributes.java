package devs.mrp.gullproject.domainsdto.propuesta.proveedor;

import java.util.List;

import javax.validation.Valid;

import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domainsdto.propuesta.AtributoForFormDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WrapPropuestaProveedorAndSelectableAttributes {

	@Valid PropuestaProveedor propuestaProveedor;
	List<AtributoForFormDto> attributes;
	
}
