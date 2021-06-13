package devs.mrp.gullproject.domains.dto;

import java.util.List;

import devs.mrp.gullproject.domains.Propuesta;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WrapPropuestaAndSelectableAttributes {

	Propuesta propuesta;
	List<AtributoForFormDto> attributes;
	
}
