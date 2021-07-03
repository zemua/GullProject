package devs.mrp.gullproject.domains.dto.linea;

import java.util.List;

import javax.validation.Valid;

import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.validator.ValidList;
import devs.mrp.gullproject.validator.ValueMatchesTipoConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineaWithAttListDto {

	public LineaWithAttListDto(Linea linea, List<AtributoForLineaFormDto> attributes) {
		this.linea = linea;
		this.attributes = attributes;
		this.qty = 1;
	}
	
	public LineaWithAttListDto(Linea linea, List<AtributoForLineaFormDto> attributes, Integer qty) {
		this.linea = linea;
		this.attributes = attributes;
		this.qty = qty;
	}
	
	@Valid
	Linea linea;
	@Valid
	List<AtributoForLineaFormDto> attributes;
	Integer qty;
	
	List<CosteLineaProveedorDto> costesProveedor;
	
}
