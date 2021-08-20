package devs.mrp.gullproject.service.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.propuesta.proveedor.FromPropuestaToProveedorFactory;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CotizacionClonerImpl implements CotizacionCloner {

	@Autowired ConsultaService consultaService;
	@Autowired LineaService lineaService;
	@Autowired LineaRepo lineaRepo;
	@Autowired FromPropuestaToProveedorFactory provFactory;
	
	@Transactional
	@Override
	public Mono<PropuestaProveedor> clone(String propClientId, String cotizacionId) {
		log.debug("to clone in proposalClient " + propClientId + " cotización " + cotizacionId);
		String cloneId = new ObjectId().toString();
		Flux<Linea> lineas = getLineas(cotizacionId, cloneId);
		Mono<PropuestaProveedor> propuesta = getCotizacion(propClientId, cotizacionId, cloneId, lineas);
		return propuesta;
	}
	
	private Flux<Linea> getLineas(String cotizacionId, String cloneId) {
		Flux<Linea> existentes = lineaService.findByPropuestaId(cotizacionId)
			.map(l -> { 
				l.setPropuestaId(cloneId);
				l.setCounterLineId(new ArrayList<>());
				l.setId(new ObjectId().toString());
				return l;
			});
		//return lineaService.addVariasLineas(existentes, cloneId); // tries to add also to the proposal and fails because it doesn't exist yet
		//return lineaRepo.saveAll(existentes); // need to add new costs Ids
		return existentes;
	}
	
	private Mono<PropuestaProveedor> getCotizacion(String propClientId, String cotizacionId, String cloneId, Flux<Linea> lineas) {
		return consultaService.findConsultaByPropuestaId(cotizacionId)
				.flatMap(c -> {
					var p = c.operations().getPropuestaById(cotizacionId);
					log.debug("cloning proposal " + p.toString());
					PropuestaProveedor prop = provFactory.from(p);
					log.debug("into " + prop.toString());
					prop.setForProposalId(propClientId);
					prop.setId(cloneId);
					Map<String, String> idToNew = new HashMap<>();
					prop.setCostes(cloneCosts(prop, idToNew));
					prop.setLineaIds(new ArrayList<>());
					return lineas.map(line -> {
						updateCostsOfLine(line, idToNew);
						prop.getLineaIds().add(line.getId());
						log.debug("added line to proposal: " + line.getId());
						return line;
					}).collectList()
							.flatMapMany(lines -> {
								return lineaRepo.saveAll(lines);
							})
					.then(consultaService.addPropuesta(c.getId(), prop)
							.map(cons -> (PropuestaProveedor)cons.operations().getPropuestaById(prop.getId())));
				});
	}
	
	private List<CosteProveedor> cloneCosts(PropuestaProveedor propuesta, Map<String, String> idtoNew) {
		List<CosteProveedor> costes = new ArrayList<>();
		if (propuesta.getCostes() == null) {propuesta.setCostes(new ArrayList<>());}
		costes.addAll(propuesta.getCostes());
		costes.stream().forEach(c -> {
			var newid = new ObjectId().toString();
			idtoNew.put(c.getId(), newid);
			c.setId(newid);
		});
		return costes;
	}
	
	private void updateCostsOfLine(Linea linea, Map<String, String> idToNew) {
		if (linea.getCostesProveedor() != null) {
			linea.getCostesProveedor().stream().forEach(c -> {
				c.setCosteProveedorId(idToNew.get(c.getCosteProveedorId()));
			});
		}
	}
	
}
