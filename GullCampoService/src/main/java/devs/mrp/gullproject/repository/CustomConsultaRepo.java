package devs.mrp.gullproject.repository;

import java.util.Map;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomConsultaRepo {

	public Mono<Consulta> addPropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Long> addVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas);
	
	public Mono<Consulta> removePropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Consulta> removeVariasPropuestas(String idConsulta, Propuesta[] propuestas);
	
	public Mono<Consulta> updateNombrePropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Long> updateNombreVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas);
	
	public Mono<Consulta> updateLineasDeUnaPropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Long> updateLineasDeVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas);
	
	public Mono<Consulta> updateUnaPropuesta(String idConsulta, Propuesta propuesta);
	
	public Mono<Long> updateVariasPropuestas(String idConsulta, Flux<Propuesta> propuestas);
	
	public Mono<Consulta> addLineaEnPropuesta(String idConsulta, String idPropuesta, String idLinea);
	
	public Mono<Consulta> removeLineaEnPropuesta(String idConsulta, String idPropuesta, String idLinea);
	
}
