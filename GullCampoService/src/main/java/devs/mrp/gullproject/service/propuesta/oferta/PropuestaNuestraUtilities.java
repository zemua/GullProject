package devs.mrp.gullproject.service.propuesta.oferta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.domains.propuestas.PvperSum;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvpOrdenable;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckbox;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvpsCheckboxWrapper;
import devs.mrp.gullproject.service.ConsultaService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PropuestaNuestraUtilities {
	
	@Autowired ConsultaService consultaService;
	@Autowired FromPropuestaToOfertaFactory toOf;
	
	public Mono<Consulta> orderPvpsInEachSum(Propuesta propuesta, List<Pvper> pvps) throws Exception {
		if (!(propuesta instanceof PropuestaNuestra)) { throw new Exception("proposal is not an offer"); }
		var sums = ((PropuestaNuestra)propuesta).getSums();
		var iterator = sums.listIterator();
		while(iterator.hasNext()) {
			var sum = iterator.next();
			orderPvps(sum, pvps);
		}
		return consultaService.updatePvpSumsOfPropuesta(propuesta.getId(), sums);
	}
	
	private PvperSum orderPvps(PvperSum sum, List<Pvper> pvps) {
		List<String> ids = new ArrayList<>();
		pvps.stream().forEach(p -> {
			if (sum.getPvperIds().contains(p.getId())) {
				ids.add(p.getId());
			}
		});
		sum.setPvperIds(ids);
		return sum;
	}
	
	public Mono<Consulta> keepUnselectedPvps(String idPropuesta, PvpsCheckboxWrapper wrapper) {
		return consultaService.removeReferenceToSelectedPvpsFromSums(idPropuesta, wrapper)
		.thenMany(consultaService.removeReferenceToSelectedPvpsFromLineas(idPropuesta, wrapper))
		.then(Mono.just(idPropuesta))
		.flatMap(rStr -> {
			return removeSelected(idPropuesta, wrapper.getPvps().stream().filter(p -> !p.isSelected()).collect(Collectors.toList()))
				.flatMap(pvps -> {
					return consultaService.updatePvpsOfPropuesta(idPropuesta, pvps);
				});
		});
	}
	
	public Mono<List<Pvper>> removeSelected(String idPropuesta, List<PvperCheckbox> pvpsCheck) {
		var existentes = getPvpsFromOfertaId(idPropuesta);
		var filtrados = existentes.map(listPvper -> {
			var iterator = listPvper.listIterator();
			while (iterator.hasNext()) {
				var pvp = iterator.next();
				if (!existe(pvpsCheck, pvp.getId())) {
					iterator.remove();
				}
			}
			return listPvper;
		});
		return filtrados;
	}
	
	private boolean existe(List<PvperCheckbox> pvps, String id) {
		var iterator = pvps.listIterator();
		while (iterator.hasNext()) {
			var pvp = iterator.next();
			if (pvp.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

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
