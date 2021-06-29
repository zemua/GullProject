package devs.mrp.gullproject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import devs.mrp.gullproject.domains.Pvper;
import devs.mrp.gullproject.domains.PvperSum;
import devs.mrp.gullproject.domains.TipoPropuesta;
import devs.mrp.gullproject.domains.dto.CostesCheckboxWrapper;
import devs.mrp.gullproject.domains.dto.PvperSumCheckboxWrapper;
import devs.mrp.gullproject.domains.dto.PvpsCheckboxWrapper;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ConsultaService {

	ConsultaRepo consultaRepo;
	ModelMapper modelMapper;
	LineaRepo lineaRepo;
	
	@Autowired
	public ConsultaService(ConsultaRepo consultaRepo, ModelMapper modelMapper, LineaRepo lineaRepo) {
		this.consultaRepo = consultaRepo;
		this.modelMapper = modelMapper;
		this.lineaRepo = lineaRepo;
	}
	
	public Mono<Consulta> save(Consulta consulta) {
		return consultaRepo.save(consulta);
	}
	
	public Flux<Consulta> findAll() {
		return consultaRepo.findAllByOrderByCreatedTimeDesc();
	}
	
	public Mono<Consulta> findById(String id) {
		return consultaRepo.findById(id);
	}
	
	public Mono<Consulta> addPropuesta(String idConsulta, Propuesta propuesta) {
		return consultaRepo.addPropuesta(idConsulta, propuesta);
	}
	
	public Mono<Long> deleteById(String id){
		return consultaRepo.deleteByIdReturningDeletedCount(id);
	}
	
	public Mono<Consulta> removePropuesta(String idConsulta, Propuesta propuesta){
		return consultaRepo.removePropuesta(idConsulta, propuesta);
	}
	
	public Mono<Consulta> removePropuestasByAssignedtoAndReturnOld(String idConsulta, String idAssignedTo) {
		return consultaRepo.removePropuestasByAssignedTo(idConsulta, idAssignedTo);
	}
	
	public Mono<Integer> removePropuestaById(String idConsulta, String idPropuesta){
		Mono<Consulta> original = findById(idConsulta);
		Mono<Propuesta> p = original.flatMap(cons -> Mono.just(cons.operations().getPropuestaById(idPropuesta)));
		Mono<Consulta> c = p.flatMap(pro -> removePropuesta(idConsulta, pro));
		Mono<Integer> deleted = c.zipWith(original, (item1, item2) -> {
			log.debug("old consulta: " + item1.toString());
			log.debug("new consulta: " + item2.toString());
			return item2.operations().getCantidadPropuestas() - item1.operations().getCantidadPropuestas();
		});
		return deleted;
	}
	
	public Flux<Propuesta> findAllPropuestasOfConsulta(String consultaId) {
		return findById(consultaId)
			.flatMapMany(c -> Flux.fromIterable(c.getPropuestas()));
	}
	
	public Mono<Propuesta> findPropuestaByPropuestaId(String propuestaId) {
		Mono<Propuesta> mono = consultaRepo.findByPropuestaId(propuestaId).flatMap(c -> Mono.just(c.operations().getPropuestaById(propuestaId)));
		return mono;
	}
	
	public Mono<Consulta> findConsultaByPropuestaId(String propuestaId) {
		return consultaRepo.findByPropuestaId(propuestaId);
	}
	
	public Flux<AtributoForCampo> findAttributesByPropuestaId(String propuestaId) {
		Flux<AtributoForCampo> flux = findPropuestaByPropuestaId(propuestaId).flatMapMany(prop -> Flux.fromIterable(prop.getAttributeColumns()));
		return flux;
	}
	
	public Flux<CosteProveedor> findCostesByPropuestaId(String propuestaId) {
		return findPropuestaByPropuestaId(propuestaId).flatMapMany(prop -> {
			if (prop instanceof PropuestaProveedor) {
				return Flux.fromIterable(((PropuestaProveedor)prop).getCostes());
			}
			else {
				return Flux.empty();
			}
		});
	}
	
	public Mono<Consulta> updateAttributesOfPropuesta(String idPropuesta, List<AtributoForCampo> attributes) {
		return consultaRepo.updateAttributesOfPropuesta(idPropuesta, attributes);
	}
	
	public Mono<Consulta> updateNameAndStatus(String idConsulta, String name, String status) {
		return consultaRepo.updateName(idConsulta, name).flatMap(c -> consultaRepo.updateStatus(idConsulta, status));
	}
	
	public Mono<Consulta> updateNombrePropuesta(Propuesta propuesta) {
		return consultaRepo.findByPropuestaId(propuesta.getId()).flatMap(rConsulta -> {
			return consultaRepo.updateNombrePropuesta(rConsulta.getId(), propuesta);
		});
	}
	
	public Mono<Consulta> reOrderAttributesOfPropuestaInConsulta(String idPropuesta, List<AtributoForCampo> attributes) {
		attributes.sort((a1, a2) -> Integer.valueOf(a1.getOrder()).compareTo(a2.getOrder()));
		return updateAttributesOfPropuesta(idPropuesta, attributes);
	}
	
	public Mono<Propuesta> reOrderAttributesOfPropuesta(String idPropuesta, List<AtributoForCampo> attributes) {
		return reOrderAttributesOfPropuestaInConsulta(idPropuesta, attributes)
				.map(consulta -> {
					return consulta.operations().getPropuestaById(idPropuesta);
				});
	}
	
	public Mono<Consulta> updateCostesOfPropuesta(String idPropuesta, List<CosteProveedor> costes) {
		return consultaRepo.updateCostesOfPropuesta(idPropuesta, costes);
	}
	
	public Mono<Consulta> addCostToList(String idPropuesta, CosteProveedor coste) {
		return consultaRepo.addCostToList(idPropuesta, coste);
	}
	
	private Flux<Consulta> removeReferenceToSelectedCostsFromPvps(String idPropuesta, CostesCheckboxWrapper wrapper) {
		return findPropuestaByPropuestaId(idPropuesta)
				.flatMapMany(pro -> {
					return getallPropuestaNuestraAsignedto(pro.getForProposalId())
							.flatMap(fPro -> {
								log.debug("para la propuesta nuestra: " + fPro.toString());
								return Flux.fromIterable(((PropuestaNuestra)fPro).getPvps())
										.map(rPvp ->{
											log.debug("el pvp: " + rPvp.toString());
											wrapper.getCostes().stream().forEach(sCost -> {
												log.debug("el coste... " + sCost.toString());
												if (sCost.isSelected() && rPvp.getIdCostes().contains(sCost.getId())) {
													rPvp.getIdCostes().remove(sCost.getId());
													log.debug("se elimina del pvp");
												}
											});
											log.debug("y devolvemos " + rPvp);
											return rPvp;
										})
										.collectList()
										.flatMap(rListPvps -> {
											log.debug("y actualizamos los pvps de la propuesta con " + rListPvps.toString());
											return updatePvpsOfPropuesta(fPro.getId(), rListPvps);
										})
										;
							});
				})
				;
	}
	
	private Flux<Linea> removeReferenceToSelectedCostsFromLineas(String idPropuesta, CostesCheckboxWrapper wrapper) {
		return lineaRepo.findAllByPropuestaIdOrderByOrderAsc(idPropuesta)
				.flatMap(fLinea -> {
					return Flux.fromIterable(wrapper.getCostes())
							.map(fCost -> {
								var ops = fLinea.operations();
								if (fCost.isSelected() && ops.ifHasCost(fCost.getId())) {
									ops.removeCosteById(fCost.getId());
								}
								return fCost;
							})
							.then(lineaRepo.updateCosts(fLinea.getId(), fLinea.getCostesProveedor()));
				})
				;
	}
	
	public Mono<Consulta> keepUnselectedCosts(String idPropuesta, CostesCheckboxWrapper wrapper) {
		return removeReferenceToSelectedCostsFromPvps(idPropuesta, wrapper)
		.thenMany(removeReferenceToSelectedCostsFromLineas(idPropuesta, wrapper))
		.then(Mono.just(idPropuesta))
		.flatMap(rStr -> {
			List<CosteProveedor> costs = wrapper.getCostes().stream().filter(c -> !c.isSelected()).map(c -> modelMapper.map(c, CosteProveedor.class)).collect(Collectors.toList());
			return updateCostesOfPropuesta(idPropuesta, costs);
		});
	}
	
	public Flux<Propuesta> getAllPropuestaProveedorAsignedTo(String propClienteId) {
		return findConsultaByPropuestaId(propClienteId)
			.flatMapMany(rCons -> {
				log.debug("consulta: " + rCons.toString());
				return Flux.fromIterable(rCons.getPropuestas().stream()
						.filter(p -> p.getTipoPropuesta().equals(TipoPropuesta.PROVEEDOR))
						.filter(p -> p.getForProposalId().equals(propClienteId))
						.collect(Collectors.toList()));
			})
			;
	}
	
	public Flux<Propuesta> getallPropuestaNuestraAsignedto(String propClienteId) {
		return findConsultaByPropuestaId(propClienteId)
				.flatMapMany(rCons -> {
					return Flux.fromIterable(rCons.getPropuestas().stream()
							.filter(p -> p.getTipoPropuesta().equals(TipoPropuesta.NUESTRA))
							.filter(p -> p.getForProposalId().equals(propClienteId))
							.collect(Collectors.toList())
							);
				})
				;
	}
	
	public Mono<Consulta> updatePvpsOfPropuesta(String idPropuesta, List<Pvper> pvps) {
		return consultaRepo.updatePvpsOfPropuesta(idPropuesta, pvps);
	}
	
	public Mono<Consulta> addPvpToList(String idPropuesta, Pvper pvp) {
		return consultaRepo.addPvpToList(idPropuesta, pvp);
	}
	
	private Mono<Consulta> removeReferenceToSelectedPvpsFromSums(String idPropuesta, PvpsCheckboxWrapper wrapper) {
		return findPropuestaByPropuestaId(idPropuesta)
		.flatMap(pro -> {
			if (pro instanceof PropuestaNuestra) {
				return Flux.fromIterable(((PropuestaNuestra)pro).getSums())
						.map(rSum -> {
							wrapper.getPvps().stream().forEach(sPvp -> {
								if (sPvp.isSelected() && rSum.getPvperIds().contains(sPvp.getId())) {
									rSum.getPvperIds().remove(sPvp.getId());
								}
							});
							return rSum;
						})
						.collectList()
						.flatMap(rListSums -> {
							return updatePvpSumsOfPropuesta(idPropuesta, rListSums);
						})
						;
			} else {
				return Mono.empty();
			}
		})
		;
	}
	
	private Flux<Linea> removeReferenceToSelectedPvpsFromLineas(String idPropuesta, PvpsCheckboxWrapper wrapper) {
		return lineaRepo.findAllByPropuestaIdOrderByOrderAsc(idPropuesta)
			.flatMap(fLinea -> {
				return Flux.fromIterable(wrapper.getPvps())
						.map(fPvp -> {
							var ops = fLinea.operations();
							if (fPvp.isSelected() && ops.ifHasPvp(fPvp.getId())) {
								ops.removePvpById(fPvp.getId());
							}
							return fPvp;
						})
						.then(lineaRepo.updatePvps(fLinea.getId(), fLinea.getPvps())
						);
			})
			;
	}
	
	public Mono<Consulta> keepUnselectedPvps(String idPropuesta, PvpsCheckboxWrapper wrapper) {
		return removeReferenceToSelectedPvpsFromSums(idPropuesta, wrapper)
		.thenMany(removeReferenceToSelectedPvpsFromLineas(idPropuesta, wrapper))
		.then(Mono.just(idPropuesta))
		.flatMap(rStr -> {
			List<Pvper> pvps = wrapper.getPvps().stream().filter(p -> !p.isSelected()).map(p -> modelMapper.map(p, Pvper.class)).collect(Collectors.toList());
			return updatePvpsOfPropuesta(idPropuesta, pvps);
		});
	}
	
	public Mono<Consulta> updatePvpSumsOfPropuesta(String idPropuesta, List<PvperSum> sums) {
		return consultaRepo.updatePvpSumsOfPropuesta(idPropuesta, sums);
	}
	
	public Mono<Consulta> addPvpSumToList(String idPropuesta, PvperSum sum) {
		return consultaRepo.addPvpSumToList(idPropuesta, sum);
	}
	
	public Mono<Consulta> keepUnselectedPvpSums(String idPropuesta, PvperSumCheckboxWrapper wrapper) {
		List<PvperSum> sums = wrapper.getSums().stream().filter(s -> !s.isSelected()).map(s -> modelMapper.map(s, PvperSum.class)).collect(Collectors.toList());
		return updatePvpSumsOfPropuesta(idPropuesta, sums);
	}
}
