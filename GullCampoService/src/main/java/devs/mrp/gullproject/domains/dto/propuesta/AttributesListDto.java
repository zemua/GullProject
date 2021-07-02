package devs.mrp.gullproject.domains.dto.propuesta;

import java.util.List;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttributesListDto {

	List<AtributoForFormDto> attributes;
	
}
