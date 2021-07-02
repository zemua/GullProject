package devs.mrp.gullproject.service.propuesta.proveedor;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.dto.BooleanWrapper;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.service.CommonOperations;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.linea.proveedor.LineToProveedorLineFactory;
import lombok.Data;
import reactor.core.publisher.Mono;

@Data
@Service
public class PropuestaProveedorUtilities {
	
	ConsultaService consultaService;
	LineaService lineaService;
	@Autowired LineToProveedorLineFactory toProveedorLine;
	
	@Autowired
	public PropuestaProveedorUtilities(ConsultaService consultaService, LineaService lineaService) {
		this.consultaService = consultaService;
		this.lineaService = lineaService;
	}

	public Mono<Boolean> ifSameNameSameCosts(String propuestaId) {
		return lineaService.findByPropuestaId(propuestaId)
			.collectList().map(list -> {
				Map<String, Linea> map = list.stream().filter(CommonOperations.distinctByKey(Linea::getNombre)).collect(Collectors.toMap(Linea::getNombre, (l) -> l));
				BooleanWrapper resultado = new BooleanWrapper(true);
				list.stream().forEach(linea -> {
					var lin = toProveedorLine.from(linea);
					var operationsDelMapa = toProveedorLine.from(map.get(lin.getNombre())).operations();
					lin.getCostesProveedor().stream().forEach(coste -> {
						CosteLineaProveedor costeDelMapa = operationsDelMapa.getCosteByCosteId(coste.getCosteProveedorId()); 
						if (costeDelMapa.getValue() != coste.getValue()) {
							resultado.setB(false);
						}
					});
				});
				return resultado.getB();
			});
	}
	
}
