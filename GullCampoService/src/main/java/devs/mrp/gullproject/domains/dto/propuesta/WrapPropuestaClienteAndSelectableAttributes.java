package devs.mrp.gullproject.domains.dto.propuesta;

import java.util.List;

import javax.validation.Valid;

import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WrapPropuestaClienteAndSelectableAttributes {

	@Valid PropuestaCliente propuestaCliente;
	List<AtributoForFormDto> attributes;
	
}
