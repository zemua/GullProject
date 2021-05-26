package devs.mrp.gullproject.domains.dto;

import java.util.List;

import devs.mrp.gullproject.domains.Campo;
import lombok.Data;

@Data
public class LineaWithSelectorDto {

	String id;
	String nombre;
	String propuestaId;
	String parentId;
	String counterLineId;
	Integer order;
	List<Campo<?>> campos;
	
	Boolean selected;
	
}
