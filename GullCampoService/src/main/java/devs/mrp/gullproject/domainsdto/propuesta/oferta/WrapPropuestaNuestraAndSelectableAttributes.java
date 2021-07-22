package devs.mrp.gullproject.domainsdto.propuesta.oferta;

import java.util.List;

import javax.validation.Valid;

import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domainsdto.propuesta.AtributoForFormDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WrapPropuestaNuestraAndSelectableAttributes {

	@Valid PropuestaNuestra propuestaNuestra;
	List<AtributoForFormDto> attributes;
	
}
