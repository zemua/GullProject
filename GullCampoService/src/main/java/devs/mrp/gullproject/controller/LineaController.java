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
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AttributesListDto;
import devs.mrp.gullproject.domains.dto.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.LineaWithAttListDto;
import devs.mrp.gullproject.domains.dto.LineaWithSelectorDto;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.ClassDestringfier;
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
	
	// TODO delete lineas like in allof with checkboxes allof->deleteof
	// TODO ordenar lineas arrastrando... Ô.ô

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
	
	@GetMapping("/of/{propuestaId}/new")
	public String addLineToPropuesta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		Linea lLinea = new Linea();
		lLinea.setPropuestaId(propuestaId);
		Mono<LineaWithAttListDto> atributosDePropuesta = getAttributesOfProposal(lLinea, propuestaId);
		model.addAttribute("propuesta", propuesta);
		model.addAttribute("propuestaId",propuestaId);
		model.addAttribute("lineaWithAttListDto", atributosDePropuesta);
		return "addLineaToPropuesta";
	}
	
	@PostMapping("/of/{propuestaId}/new")
	public Mono<String> processAddLineaToPropuesta(@Valid LineaWithAttListDto lineaWithAttListDto, BindingResult bindingResult, Model model, @PathVariable(name ="propuestaId") String propuestaId) {
		return assertBindingResultOfListDto(lineaWithAttListDto, bindingResult)
			.then(Mono.just(bindingResult)).flatMap(rBindingResult -> {
				if(rBindingResult.hasErrors()) {
					model.addAttribute("propuesta", consultaService.findPropuestaByPropuestaId(propuestaId));
					model.addAttribute("propuestaId", propuestaId);
					model.addAttribute("lineaWithAttListDto", lineaWithAttListDto);
					return Mono.just("addLineaToPropuesta");
				} else {
					Mono<Linea> l1;
					Mono<Propuesta> p1;
					if (lineaWithAttListDto.getLinea().getPropuestaId().equals(propuestaId)){
						log.debug("propuestaId's equal");
						Mono<Linea> llinea = reconstructLine(lineaWithAttListDto);
						l1 = lineaService.addLinea(llinea);
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
	
	@GetMapping("/revisar/id/{lineaid}")
	public String revisarLinea(Model model, @PathVariable(name ="lineaid") String lineaId) {
		Mono<Linea> linea = lineaService.findById(lineaId);
		Mono<LineaWithAttListDto> lineaDto = getAttributesOfProposal(linea);
		
		model.addAttribute("lineaWithAttListDto", lineaDto);
		
		return "reviewLinea";
	}
	
	@PostMapping("/revisar/id/{lineaid}")
	public Mono<String> processRevisarLinea(@Valid LineaWithAttListDto lineaWithAttListDto, BindingResult bindingResult, Model model, @PathVariable(name="lineaid") String lineaId) {
		return assertBindingResultOfListDto(lineaWithAttListDto, bindingResult)
				.then(Mono.just(bindingResult)).flatMap(rBindingResult -> {
					if (rBindingResult.hasErrors()) {
						model.addAttribute("lineaWithAttListDto",lineaWithAttListDto);
						return Mono.just("reviewLinea");
					} else {
						Mono<Linea> l1;
						if (lineaWithAttListDto.getLinea().getId().equals(lineaId)){
							log.debug("propuestaId's equal");
							Mono<Linea> llinea = reconstructLine(lineaWithAttListDto);
							l1 = llinea.flatMap(rLlinea -> {
								return lineaService.updateLinea(rLlinea);
							});
						} else {
							log.debug("propuestaId's are NOT equal");
							l1 = Mono.empty();
						}
						model.addAttribute("linea", l1);
						return Mono.just("processReviewLine");
					}
				});
	}
	
	
	@GetMapping("/delete/id/{lineaid}")
	public String deleteLinea(Model model, @PathVariable(name ="lineaid") String lineaId) {
		Mono<Linea> linea = lineaService.findById(lineaId);
		model.addAttribute("linea", linea);
		return "deleteLinea";
	}
	
	@PostMapping("/delete/id/{lineaid}")
	public String processDeleteLinea(Linea linea, BindingResult bindingResult, Model model, @PathVariable(name="lineaid") String lineaId) {
		Mono<Long> deleteCount;
		if (linea.getId().equals(lineaId)) {
			deleteCount = lineaService.deleteLineaById(lineaId);
		} else {
			deleteCount = Mono.just(0L);
		}
		model.addAttribute("deleteCount", deleteCount);
		model.addAttribute("idPropuesta", linea.getPropuestaId());
		return "processDeleteLinea";
	}
	
	@GetMapping("/deleteof/propid/{propuestaId}") // TODO test
	public String deleteLinesOf(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Flux<LineaWithSelectorDto> lineas = lineaService.findByPropuestaId(propuestaId)
				.map(rl -> {
					LineaWithSelectorDto dto = modelMapper.map(rl, LineaWithSelectorDto.class);
					dto.setSelected(false);
					return dto;
				});
		model.addAttribute("lineas", new ReactiveDataDriverContextVariable(lineas, 1));
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "deleteLinesOf"; // TODO make template
	}
	
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * Functional operations
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	private Mono<LineaWithAttListDto> getAttributesOfProposal(Linea lLinea, String propuestaId){
		return consultaService.findAttributesByPropuestaId(propuestaId)
				.map(rAttProp -> modelMapper.map(rAttProp, AtributoForLineaFormDto.class))
				.map(rAttForForm -> {
					rAttForForm.setValue(lLinea.getValueByAttId(rAttForForm.getId()));
					return rAttForForm;
				})
				.collectList()
				.flatMap(rAttFormList -> Mono.just(new LineaWithAttListDto(lLinea, new ValidList<AtributoForLineaFormDto>(rAttFormList))));
	}
	
	private Mono<LineaWithAttListDto> getAttributesOfProposal(Mono<Linea> lLinea){
		return lLinea.flatMap(linea -> getAttributesOfProposal(linea, linea.getPropuestaId()));
	}
	
	private Flux<Boolean> assertBindingResultOfListDto(LineaWithAttListDto lineaWithAttListDto, BindingResult bindingResult) {
		/**
		 * BindingResult checks out of the box if there is any error in the line, but not in the attributes (we removed the validation in that class)
		 * To check if the attributes are correct we need to call the attribute repository, which returns a Reactor object
		 * The class AttributeValueValidator.class can validate the result, but it needs to block the reactor object in a flux thread
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
					log.debug("att type: " + rAtt.getTipo());
					log.debug("at value: " + rAtt.getValue());
					return atributoService.validateDataFormat(rAtt.getTipo(), rAtt.getValue().replace(",", ".")) // we replace , with . in case it is a decimal number, as in Europe "," is used
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
			});
	}
	
	private Mono<Linea> reconstructLine(LineaWithAttListDto lineaWithAttListDto) {
		Linea nLinea = lineaWithAttListDto.getLinea();
		return Flux.fromIterable(lineaWithAttListDto.getAttributes())
				.flatMap(rAtt -> atributoService.getClassTypeOfFormat(rAtt.getTipo()).map(
						rClass -> new Campo<Object>(rAtt.getId(), ClassDestringfier.toObject(rClass, rAtt.getValue()))))
				.collectList().map(rCampoList -> {
					nLinea.replaceOrAddCamposObj(rCampoList);
					return nLinea;
				});
	}
	
}
