package devs.mrp.gullproject.service.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.propuesta.ProposalIdsMergerFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.PropuestaProveedorExtractor;
import reactor.core.publisher.Flux;

@Service
public class SupplierLineFinderByProposalAssignation implements MyFinder<Flux<Linea>, String> {

	@Autowired ConsultaService consultaService;
	@Autowired LineaService lineaService;
	@Autowired ProposalIdsMergerFactory<PropuestaProveedor> mergerFactory;
	@Autowired PropuestaProveedorExtractor extractor;

	@Override
	public Flux<Linea> findBy(String assignedProposalId) {
		var consulta = consultaService.findConsultaByPropuestaId(assignedProposalId);
		return consulta.flatMapMany(rCons -> {
			var props = rCons.operations().getAllPropuestasAssignedToId(assignedProposalId);
			var proveedores = extractor.filter(Flux.fromIterable(props));
			return proveedores.collectList().flatMapMany(rProv -> {
				var proposalIdsMerger = mergerFactory.from(rProv);
				return lineaService.findBySeveralPropuestaIds(proposalIdsMerger.merge());
			});
		});
	}
	
}
