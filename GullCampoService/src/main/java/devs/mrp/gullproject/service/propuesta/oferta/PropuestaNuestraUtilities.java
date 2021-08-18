package devs.mrp.gullproject.service.propuesta.oferta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvpOrdenable;
import devs.mrp.gullproject.service.ConsultaService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PropuestaNuestraUtilities {
	
	@Autowired ConsultaService consultaService;
	@Autowired FromPropuestaToOfertaFactory toOf;

	public Mono<List<Pvper>> fromPvpsOrdenablesToPvper(String ofertaId, List<PvpOrdenable> pvps) throws Exception {
		pvps.sort((p1, p2) -> Integer.valueOf(p1.getOrder()).compareTo(p2.getOrder()));
		var existentes = getPvpsFromOfertaId(ofertaId);
		return existentes.map(pvpList -> {
			List<Pvper> result = new ArrayList<>();
			pvps.stream().forEach(p -> {
				try {
					result.add(findById(pvpList, p.getId()).orElseThrow(() -> new Exception("pvp doesn't exist in the db")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			return result;
		});
	}
	
	public Mono<List<Pvper>> getPvpsFromOfertaId(String ofertaId) {
		var oferta = consultaService.findPropuestaByPropuestaId(ofertaId)
				.map(of -> {
					if (of instanceof PropuestaNuestra) {
						return toOf.from(of);
					}
					return null;
				});
		return oferta.map(of -> {
			if (of == null) { return null; }
			return of.getPvps();
		});
	}
	
	private Optional<Pvper> findById(List<Pvper> pvpers, String id) {
		var iterable = pvpers.listIterator();
		while (iterable.hasNext()) {
			var pvp = iterable.next();
			if (pvp.getId().equals(id)) {
				return Optional.of(pvp);
			}
		}
		return Optional.empty();
	}
	
}
