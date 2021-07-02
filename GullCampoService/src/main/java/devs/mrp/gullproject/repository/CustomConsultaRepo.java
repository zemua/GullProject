package devs.mrp.gullproject.repository;

import java.util.List;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.domains.propuestas.PvperSum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomConsultaRepo {

	public Mono<Consulta> findByPropuestaId(String propuestaId);
	
	public Mono<Consulta> addPropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Long> addVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas);
	
	public Mono<Consulta> removePropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Consulta> removePropuestasByAssignedTo(String idConsulta, String idAssignedTo);
	
	public Mono<Consulta> removeVariasPropuestas(String idConsulta, Propuesta[] propuestas);
	
	public Mono<Consulta> updateNombrePropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Long> updateNombreVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas);
	
	public Mono<Consulta> updateLineasDeUnaPropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Long> updateLineasDeVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas);
	
	public Mono<Consulta> updateUnaPropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Long> updateVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas);
	
	public Mono<Consulta> addLineaEnPropuesta(String idConsulta, String idPropuesta, String idLinea);
	
	public Mono<Consulta> removeLineaEnPropuesta(String idConsulta, String idPropuesta, String idLinea);
	
	public Mono<Consulta> updateName(String idConsulta, String name);
	
	public Mono<Consulta> updateStatus(String idConsulta, String status);
	
	public Mono<Consulta> addAttributeToList(String idPropuesta, AtributoForCampo attribute);
	
	public Mono<Consulta> removeAttributeFromList(String idPropuesta, AtributoForCampo attribute);
	
	public Mono<Consulta> updateAttributesOfPropuesta(String idPropuesta, List<AtributoForCampo> attributes);
	
	public Mono<Consulta> updateCostesOfPropuesta(String idPropuesta, List<CosteProveedor> costes);
	
	public Mono<Consulta> addCostToList(String idPropuesta, CosteProveedor coste);
	
	public Mono<Consulta> updatePvpsOfPropuesta(String idPropuesta, List<Pvper> pvps);
	
	public Mono<Consulta> addPvpToList(String idPropuesta, Pvper pvp);
	
	public Mono<Consulta> updatePvpSumsOfPropuesta(String idPropuesta, List<PvperSum> sums);
	
	public Mono<Consulta> addPvpSumToList(String idPropuesta, PvperSum sum);
	
}
