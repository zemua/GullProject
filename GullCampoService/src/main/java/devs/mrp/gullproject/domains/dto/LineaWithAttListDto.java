package devs.mrp.gullproject.domains.dto;

import java.util.List;

import javax.validation.Valid;

import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.validator.ValidList;
import devs.mrp.gullproject.validator.ValueMatchesTipoConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineaWithAttListDto {

	@Valid
	Linea linea;
	@Valid
	List<AtributoForLineaFormDto> attributes;
	Integer qty;
	
}
