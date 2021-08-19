package devs.mrp.gullproject.domainsdto.propuesta.oferta;

import java.util.List;

import devs.mrp.gullproject.domains.propuestas.Pvper.IdAttsList;
import lombok.Data;

@Data
public class PvpOrdenable {

	String id;
	List<String> idCostes;
	List<IdAttsList> idAttributesByCotiz;
	String name;
	int order;
	
}
