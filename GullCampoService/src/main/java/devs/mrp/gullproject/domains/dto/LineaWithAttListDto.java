package devs.mrp.gullproject.domains.dto;

import java.util.List;

import devs.mrp.gullproject.domains.Linea;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineaWithAttListDto {

	Linea linea;
	List<AtributoForLineaFormDto> attributes;
	
}
