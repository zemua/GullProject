package devs.mrp.gullproject.domains.dto;

import java.util.List;

import javax.validation.Valid;

import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WrapPropuestaNuestraAndSelectableAttributes {

	@Valid PropuestaNuestra propuestaNuestra;
	List<AtributoForFormDto> attributes;
	
}
