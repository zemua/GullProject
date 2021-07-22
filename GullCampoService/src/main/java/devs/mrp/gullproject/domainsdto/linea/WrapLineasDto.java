package devs.mrp.gullproject.domainsdto.linea;

import java.util.List;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaImpl;
import lombok.Data;

@Data
public class WrapLineasDto {

	List<Linea> lineas;
	
}
