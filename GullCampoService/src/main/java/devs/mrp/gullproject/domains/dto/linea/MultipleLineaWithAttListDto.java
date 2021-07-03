package devs.mrp.gullproject.domains.dto.linea;

import java.util.List;

import devs.mrp.gullproject.domains.linea.Linea;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MultipleLineaWithAttListDto {

	List<LineaWithAttListDto> lineaWithAttListDtos;
	
}
