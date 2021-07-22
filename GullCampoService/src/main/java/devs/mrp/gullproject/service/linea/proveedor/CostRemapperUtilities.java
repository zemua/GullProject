package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domainsdto.linea.proveedor.CostRemapper;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Data
@Slf4j
@Service
public class CostRemapperUtilities {

	LineaService lineaService;
	
	@Autowired
	CostRemapperUtilities(LineaService lineaService) {
		this.lineaService = lineaService;
	}
	
	public Flux<Linea> remapLineasCost(List<CostRemapper> remapers, String propuestaId) {
		if (remapers.size() == 0) { return null; }
		String costId = remapers.get(0).getCosteProveedorId();
		
		return Flux.fromIterable(remapers)
				.collectMap((rMapper)-> rMapper.getBefore(), (rMapper) -> rMapper)
				.flatMapMany(rMap -> {
					return lineaService.findByPropuestaId(propuestaId)
							.flatMap(rLinea -> {
								CosteLineaProveedor cos = rLinea.operations().getCosteByCosteId(costId);
								if (rMap.containsKey(cos.getValue())) {
									cos.setValue(rMap.get(cos.getValue()).getAfter());
								}
								return lineaService.updateLinea(rLinea);
							})
							;
				})
				;
	}
	
}
