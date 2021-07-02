package devs.mrp.gullproject.domains.dto.propuesta.oferta;

import java.util.List;

import javax.validation.Valid;

import devs.mrp.gullproject.domains.dto.propuesta.AtributoForFormDto;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WrapPropuestaNuestraAndSelectableAttributes {

	@Valid PropuestaNuestra propuestaNuestra;
	List<AtributoForFormDto> attributes;
	
}
