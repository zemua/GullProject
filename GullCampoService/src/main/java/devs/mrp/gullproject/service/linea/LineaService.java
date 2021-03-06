package devs.mrp.gullproject.service.linea;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.repository.ConsultaRepo;
import devs.mrp.gullproject.repository.LineaRepo;
import devs.mrp.gullproject.service.facade.ConsultaAndLinesFacade;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LineaService {

	private LineaRepo lineaRepo;
	private ConsultaRepo consultaRepo;
	
	@Autowired
	public LineaService(LineaRepo lineaRepo, ConsultaRepo consultaRepo) {
		this.lineaRepo = lineaRepo;
		this.consultaRepo = consultaRepo;
	}
	
	public Mono<Linea> findById(String id){
		return lineaRepo.findById(id);
	}
	
	public Flux<Linea> findByPropuestaId(String propuestaId) {
		return lineaRepo.findAllByPropuestaIdOrderByOrderAsc(propuestaId);
	}
	
	public Flux<Linea> findBySeveralPropuestaIds(List<String> propuestaIds) {
		log.debug("lineaService, findBySeveralPropuestaIds: " + propuestaIds.toString());
		return lineaRepo.findLineasByPropuestaIdIn(propuestaIds);
	}
	
	public Flux<Linea> findAll(){
		return lineaRepo.findAllByOrderByOrderAsc();
	}
	
	public Mono<Linea> addCampo(String idLinea, Campo<?> campo){
		return lineaRepo.addCampo(idLinea, campo);
	}
	
	public Mono<Long> addVariosCampos(String idLinea, Flux<Campo<?>> campos){		
		return lineaRepo.addVariosCampos(idLinea, campos);
	}
	
	public Mono<Linea> removeCampo(String idLinea, Campo<?> campo){
		return lineaRepo.removeCampo(idLinea, campo);
	}
	
	public Mono<Linea> removeVariosCampos(String idLinea, Campo<?>[] campos){
		return lineaRepo.removeVariosCampos(idLinea, campos);
	}
	
	public Mono<Linea> addLinea(Linea linea) {
		String nPropuestaId = linea.getPropuestaId();
		return consultaRepo.findByPropuestaId(nPropuestaId)
				.map(consulta -> consulta.getId())
				.flatMap(rConsultaId -> consultaRepo.addLineaEnPropuesta(rConsultaId, nPropuestaId, linea.getId()))
				
				.then(lineaRepo.findFirstByPropuestaIdOrderByOrderDesc(nPropuestaId))
				.switchIfEmpty(Mono.just(linea).map(rLi -> {rLi.setOrder(0); return rLi;}))
				.flatMap(rLinea -> {
					if (rLinea == null || rLinea.getOrder() == null) {
						linea.setOrder(1);
					} else {
						linea.setOrder(rLinea.getOrder()+1);
					}
					return lineaRepo.insert(linea);
				});
	}
	
	public Mono<Linea> addLinea(Mono<Linea> linea) {
		return linea.flatMap(l -> addLinea(l));
	}
	
	public Flux<Linea> addVariasLineas(Flux<Linea> lineas, String propuestaId) {
		log.debug("call to AddVariasLineas");
		AtomicInteger count = new AtomicInteger();
		return lineas.collectList().flatMapMany(rLineasList -> {
			return consultaRepo.findByPropuestaId(propuestaId)
					.flatMapMany(rConsulta ->{
						return Flux.fromIterable(rLineasList)
								.flatMap(rLinea -> {
									return consultaRepo.addLineaEnPropuesta(rConsulta.getId(), propuestaId, rLinea.getId());
								})
								.then(lineaRepo.countByPropuestaId(propuestaId)).flatMapMany(rCount -> {
									count.set(rCount.intValue());
									return Flux.fromIterable(rLineasList);
									})
								.map(rLinea -> {
									rLinea.setOrder(count.incrementAndGet());
									return rLinea;
									})
								.collectList().flatMapMany(rList -> {
									log.debug("to insert in lines: " + rList);
									return lineaRepo.insert(rList);
									});
					});
		});
	}
	
	public Mono<Linea> updateLinea(Linea linea) {
		return lineaRepo.save(linea);
	}
	
	public Flux<Linea> updateVariasLineas(Flux<Linea> lineas) {
		return lineaRepo.saveAll(lineas);
	}
	
	public Mono<Void> deleteLineaById(String id) {
		Mono<Linea> nlinea = lineaRepo.findById(id);
		Mono<String> nPropuestaId = nlinea.map(linea -> linea.getPropuestaId());
		Mono<String> consultaId = nlinea.flatMap(linea -> consultaRepo.findByPropuestaId(linea.getPropuestaId()).map(consulta -> consulta.getId()));
		return Mono.zip(consultaId, nPropuestaId).flatMap(t -> consultaRepo.removeLineaEnPropuesta(t.getT1(), t.getT2(), id))
				.then(lineaRepo.deleteById(id));		
	}
	
	public Mono<Void> deleteVariasLineasById(Flux<String> ids){
		return deleteVariasLineas(ids.collectList().flatMapMany(rIdsList -> lineaRepo.findLineasByIdInOrderByOrderAsc(rIdsList)));
	}
	
	public Mono<Void> deleteVariasLineas(Flux<Linea> lineas) {
		return lineas.flatMap(rLinea -> consultaRepo.findByPropuestaId(rLinea.getPropuestaId())
														.flatMap(rConsulta -> consultaRepo.removeLineaEnPropuesta(rConsulta.getId(), rLinea.getPropuestaId(), rLinea.getId())))
				.then(lineaRepo.deleteAll(lineas));
	}
	
	public Mono<Void> updateOrderOfSeveralLineas(Map<String, Integer> idlineaVSposicion) {
		Set<String> set = idlineaVSposicion.keySet();
		set.stream().forEach(id -> {
			lineaRepo.updateOrder(id, idlineaVSposicion.get(id)).subscribe();
		});
		return Mono.empty();
	}
	
	private Mono<Consulta> deleteAllLineasFromPropuesta(String propuestaId) {
		return consultaRepo.findByPropuestaId(propuestaId).flatMap(rConsulta -> {
			Propuesta prop = rConsulta.operations().getPropuestaById(propuestaId);
			prop.setLineaIds(new ArrayList<>());
			return consultaRepo.updateLineasDeUnaPropuesta(rConsulta.getId(), prop);
		});
	}
	
	public Mono<Long> deleteSeveralLineasFromPropuestaId(String propuestaId){
		return deleteAllLineasFromPropuesta(propuestaId)
				.then(lineaRepo.deleteSeveralLineasByPropuestaId(propuestaId).map(r -> Long.valueOf(r.getDeletedCount())));
	}
	
	public Mono<Long> deleteSeveralLineasFromSeveralPropuestaIds(List<String> propuestaIds) {
		return Flux.fromIterable(propuestaIds).flatMap(rPropuestaId -> deleteAllLineasFromPropuesta(rPropuestaId))
			.then(lineaRepo.deleteSeveralLineasBySeveralPropuestaIds(propuestaIds).map(r-> Long.valueOf(r.getDeletedCount())));
	}
	
	public Mono<Long> deleteSeveralLineasFromSeveralPropuestas(List<Propuesta> propuestas) {
		List<String> ids = propuestas.stream().map(arg0 -> arg0.getId()).collect(Collectors.toList());
		return deleteSeveralLineasFromSeveralPropuestaIds(ids);
	}
	
	public Mono<Linea> updateNombre(String idLinea, String nombre) {
		return lineaRepo.updateNombre(idLinea, nombre);
	}
	
	public Mono<Linea> updateCounterLineId(String idLinea, List<String> counterLineId) {
		return lineaRepo.updateCounterLineId(idLinea, counterLineId);
	}
	
	public Mono<Linea> addCounterLineId(String idLinea, String counterLineId) {
		return lineaRepo.addCounterLineId(idLinea, counterLineId);
	}
	
	public Mono<Linea> removeCounterLineId(String idLinea, String counterLineId) {
		return lineaRepo.removeCounterLineId(idLinea, counterLineId);
	}
	
	public Mono<Linea> updatePvps(String idLinea, List<PvperLinea> pvps) {
		return lineaRepo.updatePvps(idLinea, pvps);
	}
	
}
