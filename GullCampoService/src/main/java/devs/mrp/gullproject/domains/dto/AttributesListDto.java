package devs.mrp.gullproject.domains.dto;

import java.util.List;

import devs.mrp.gullproject.domains.AtributoForCampo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttributesListDto {

	List<AtributoForFormDto> attributes;
	
}
