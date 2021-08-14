package devs.mrp.gullproject.service.linea.cliente;

import java.util.List;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domainsdto.linea.QtyRemapper;
import reactor.core.publisher.Flux;

public interface QtyRemapperUtilities {

	public Flux<Linea> remapLineasQty(List<QtyRemapper> remapers, String propuestaId);
	
}
