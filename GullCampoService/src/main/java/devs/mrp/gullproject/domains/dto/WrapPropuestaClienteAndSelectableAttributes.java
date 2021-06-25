package devs.mrp.gullproject.domains.dto;

import java.util.List;

import javax.validation.Valid;

import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WrapPropuestaClienteAndSelectableAttributes {

	@Valid PropuestaCliente propuestaCliente;
	List<AtributoForFormDto> attributes;
	
}
