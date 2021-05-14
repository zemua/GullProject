package devs.mrp.gullproject.controller;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.configuration.ClientProperties;
import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AttributesListDto;
import devs.mrp.gullproject.domains.dto.BooleanWrapper;
import devs.mrp.gullproject.domains.dto.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.LineaWithAttListDto;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import devs.mrp.gullproject.validator.AttributeValueValidator;
import devs.mrp.gullproject.validator.ValidList;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping(path = "/lineas")
public class LineaController {
	
	// TODO make method for /revisar/id/{lineaid}

	private LineaService lineaService;
	private ConsultaService consultaService;
	private ModelMapper modelMapper;
	AtributoServiceProxyWebClient atributoService;
	
	@Autowired
	public LineaController(LineaService lineaService, ConsultaService consultaService, ModelMapper modelMapper, AtributoServiceProxyWebClient atributoService) {
		this.lineaService = lineaService;
		this.consultaService = consultaService;
		this.modelMapper = modelMapper;
		this.atributoService = atributoService;
	}
	
	@GetMapping("/allof/propid/{propuestaId}")
	public String showAllLinesOf(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Flux<Linea> lineas = lineaService.findByPropuestaId(propuestaId);
		model.addAttribute("lineas", new ReactiveDataDriverContextVariable(lineas, 1));
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId",propuestaId);
		return "showAllLineasOfPropuesta";
	}
	
	@GetMapping("/of/{propuestaId}/new") // TODO mostrar error cuando datos no coinciden
	public String addLineToPropuesta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		Linea lLinea = new Linea();
		lLinea.setPropuestaId(propuestaId);
		Mono<LineaWithAttListDto> atributosDePropuesta = consultaService.findAttributesByPropuestaId(propuestaId)
														.map(rAttProp -> modelMapper.map(rAttProp, AtributoForLineaFormDto.class))
														.collectList()
														.flatMap(rAttFormList -> Mono.just(new LineaWithAttListDto(lLinea, new ValidList<AtributoForLineaFormDto>(rAttFormList))));
		model.addAttribute("propuesta", propuesta);
		model.addAttribute("propuestaId",propuestaId);
		model.addAttribute("lineaWithAttListDto", atributosDePropuesta); // TODO test
		return "addLineaToPropuesta";
	}
	
	@PostMapping("/of/{propuestaId}/new") // TODO save the attributes received with the linea
	public Mono<String> processAddLineaToPropuesta(@Valid LineaWithAttListDto lineaWithAttListDto, BindingResult bindingResult, Model model, @PathVariable(name ="propuestaId") String propuestaId) {
		
		/**
		 * BindingResult checks out of the box if there is any error in the line, but not in the attributes (we removed the validation)
		 * To check if the attributes are correct we need to call the attribute repository, which returns a Reactor object
		 * The class AttributeValueValidator.class can check that, but it needs to block the reactor object in a flux thread
		 * So we need to add errors manually to the bindingResult in a flow and return a Mono to avoid blocking
		 */
		Map<AtributoForLineaFormDto, Integer> map = new HashMap<>();
		return Mono.just(lineaWithAttListDto.getAttributes())
			.map(rAttList -> {
				for (int i = 0; i<rAttList.size(); i++) {
					map.put(rAttList.get(i), i);
				}
				return rAttList;
			}).flatMapMany(rAttList -> Flux.fromIterable(rAttList))
			.flatMap(rAtt -> {
				if (!rAtt.getValue().isBlank()) {
					log.debug(rAtt.getValue());
					return atributoService.validateDataFormat(rAtt.getTipo(), rAtt.getValue())
							.map(rBool -> {
								if(!rBool) {
									Integer pos = map.get(rAtt);
									bindingResult.rejectValue("attributes[" + pos + "].id", "error.atributosDeLinea.attributes[" + pos + "]", "El valor no es correcto para este atributo.");
								}
								return rBool;
							});
				} else {
					return Mono.just(true);
				}
			})
			.then(Mono.just(bindingResult)).flatMap(rBindingResult -> {
				if(rBindingResult.hasErrors()) {
					model.addAttribute("propuesta", consultaService.findPropuestaByPropuestaId(propuestaId));
					model.addAttribute("propuestaId", propuestaId);
					model.addAttribute("lineaWithAttListDto", lineaWithAttListDto);
					return Mono.just("addLineaToPropuesta");
				} else {
					// TODO add the attributes to the line before saving
					
					Mono<Linea> l1;
					Mono<Propuesta> p1;
					if (lineaWithAttListDto.getLinea().getPropuestaId().equals(propuestaId)){
						log.debug("propuestaId's equal");
						l1 = lineaService.addLinea(lineaWithAttListDto.getLinea());
						p1 = consultaService.findPropuestaByPropuestaId(propuestaId);
					} else {
						log.debug("propuestaId's are NOT equal");
						l1 = Mono.empty();
						p1 = Mono.empty();
					}
					model.addAttribute("linea", l1);
					model.addAttribute("propuesta", p1);
					return Mono.just("processAddLineaToPropuesta");
				}
			});
	}
	
}
