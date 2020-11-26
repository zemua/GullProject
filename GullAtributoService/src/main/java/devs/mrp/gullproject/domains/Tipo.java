package devs.mrp.gullproject.domains;

import lombok.Data;

@Data
public class Tipo {
	
	String nombre;
	
	DataFormat dataFormat;
	
	
	public enum DataFormat {
		atributoTexto, cantidad, coste, margen, pvp, plazo;
	}
	
}
