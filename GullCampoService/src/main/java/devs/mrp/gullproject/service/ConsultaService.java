package devs.mrp.gullproject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import devs.mrp.gullproject.domains.Pvper;
import devs.mrp.gullproject.domains.TipoPropuesta;
import devs.mrp.gullproject.domains.dto.CostesCheckboxWrapper;
import devs.mrp.gullproject.domains.dto.PvpsCheckboxWrapper;
import devs.mrp.gullproject.repository.ConsultaRepo;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ConsultaService {

	ConsultaRepo consultaRepo;
	ModelMapper modelMapper;
	
	@Autowired
	public ConsultaService(ConsultaRepo consultaRepo, ModelMapper modelMapper) {
		this.consultaRepo = consultaRepo;
		this.modelMapper = modelMapper;
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
	
	public Mono<Consulta> keepUnselectedCosts(String idPropuesta, CostesCheckboxWrapper wrapper) {
		List<CosteProveedor> costs = wrapper.getCostes().stream().filter(c -> !c.isSelected()).map(c -> modelMapper.map(c, CosteProveedor.class)).collect(Collectors.toList());
		return updateCostesOfPropuesta(idPropuesta, costs);
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
	
	public Mono<Consulta> updatePvpsOfPropuesta(String idPropuesta, List<Pvper> pvps) {
		return consultaRepo.updatePvpsOfPropuesta(idPropuesta, pvps);
	}
	
	public Mono<Consulta> addPvpToList(String idPropuesta, Pvper pvp) {
		return consultaRepo.addPvpToList(idPropuesta, pvp);
	}
	
	public Mono<Consulta> keepUnselectedPvps(String idPropuesta, PvpsCheckboxWrapper wrapper) { // TODO test
		List<Pvper> pvps = wrapper.getPvps().stream().filter(p -> !p.isSelected()).map(p -> modelMapper.map(p, Pvper.class)).collect(Collectors.toList());
		return updatePvpsOfPropuesta(idPropuesta, pvps);
	}
}
