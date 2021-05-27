package devs.mrp.gullproject.domains.dto;

import java.util.List;

import lombok.Data;

@Data
public class WrapLineasWithSelectorDto {

	List<LineaWithSelectorDto> lineas;
	Boolean confirmDelete = false;
	
}
