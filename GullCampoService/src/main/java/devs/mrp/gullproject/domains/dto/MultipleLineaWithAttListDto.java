package devs.mrp.gullproject.domains.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MultipleLineaWithAttListDto {

	List<LineaWithAttListDto> lineaWithAttListDtos;
	
}
