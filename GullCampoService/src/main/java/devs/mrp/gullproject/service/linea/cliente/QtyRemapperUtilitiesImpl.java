package devs.mrp.gullproject.service.linea.cliente;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domainsdto.linea.QtyRemapper;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.Data;
import reactor.core.publisher.Flux;

@Data
@Component
public class QtyRemapperUtilitiesImpl implements QtyRemapperUtilities {

	@Autowired
	private LineaService lineaService;
	
	@Override
	public Flux<Linea> remapLineasQty(List<QtyRemapper> remapers, String propuestaId) {
		if (remapers.size() == 0) {return null;}
		
		return Flux.fromIterable(remapers)
				.collectMap((rMapper) -> rMapper.getBefore(), (rMapper) -> rMapper)
				.flatMapMany(rMap -> {
					return lineaService.findByPropuestaId(propuestaId)
							.flatMap(rLinea -> {
								if (rLinea.getQty() == null) {rLinea.setQty(1);}
								int qty = rLinea.getQty();
								if (rMap.containsKey(qty)) {
									rLinea.setQty(rMap.get(qty).getAfter());
								}
								return lineaService.updateLinea(rLinea);
							})
							;
				})
				;
	}

}
