package devs.mrp.gullproject.domains.dto.linea;

import java.util.List;

import devs.mrp.gullproject.domains.linea.Linea;
import lombok.Data;

@Data
public class WrapLineasDto {

	List<Linea> lineas;
	
}