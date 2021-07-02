package devs.mrp.gullproject.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.dto.propuesta.AttRemaper;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.service.linea.LineaOperations;
import devs.mrp.gullproject.service.linea.LineaService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Slf4j
@Service
public class AttRemaperUtilities {

	AtributoServiceProxyWebClient atributoService;
	LineaService lineaService;
	
	@Autowired
	public AttRemaperUtilities(AtributoServiceProxyWebClient atributoService, LineaService lineaService) {
		this.atributoService = atributoService;
		this.lineaService = lineaService;
	}
	
	public Mono<Boolean> validateAttRemaper(AttRemaper attRemaper) {
		return atributoService.validateDataFormat(attRemaper.getTipo(), attRemaper.getAfter());
	}
	
	public Flux<Boolean> validateAttRemapers(List<AttRemaper> remapers, BindingResult bindingResult, String errorRoute) {
		return Flux.fromIterable(remapers).index()
			.flatMap(rRemaper -> {
				return validateAttRemaper(rRemaper.getT2())
						.map(rBol -> {
							log.debug("error found: " + !rBol + " / for: " + rRemaper.getT2());
							if (!rBol) {
								bindingResult.rejectValue(errorRoute + "[" + rRemaper.getT1() + "].after",
										"error." + errorRoute + "[" + rRemaper.getT1() + "]",
										"El valor no es correcto.");
							}
							return rBol;
						});
			})
			;
	}
	
	public Flux<Linea> remapLineasAtt(List<AttRemaper> remapers, String propuestaId) {
		if (remapers.size() == 0) {return null;}
		String attId = remapers.get(0).getAtributoId();
		
		return atributoService.getClassTypeOfFormat(remapers.get(0).getTipo())
				.flatMap(rClase -> {
					return Flux.fromIterable(remapers)
						.map(rMapper -> {
							rMapper.setClase(rClase);
							rMapper.setAfterObj(ClassDestringfier.toObject(rMapper.getClase(), rMapper.getAfter()));
							return rMapper;
						})
						.collectMap((rMapper) -> rMapper.getBefore(), (rMapper) -> rMapper)
						;
				})
				.flatMapMany(rMap -> {
					return lineaService.findByPropuestaId(propuestaId)
							.flatMap(rLinea -> {
								LineaOperations ops = rLinea.operations();
								Campo<?> campo = ops.getCampoByAttId(attId);
								if(rMap.containsKey(campo.getDatosText())) {
									campo.setDatosCasting(rMap.get(campo.getDatosText()).getAfterObj());
								}
								return lineaService.updateLinea(rLinea);
							})
							;
				})
				;
	}
	
}
