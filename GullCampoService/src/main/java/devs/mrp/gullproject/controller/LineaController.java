package devs.mrp.gullproject.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import devs.mrp.gullproject.domains.StringListOfListsWrapper;
import devs.mrp.gullproject.domains.StringListWrapper;
import devs.mrp.gullproject.domains.StringWrapper;
import devs.mrp.gullproject.domains.WrapLineasDto;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AttributesListDto;
import devs.mrp.gullproject.domains.dto.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.LineaWithAttListDto;
import devs.mrp.gullproject.domains.dto.LineaWithSelectorDto;
import devs.mrp.gullproject.domains.dto.WrapLineasWithSelectorDto;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.ClassDestringfier;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import devs.mrp.gullproject.service.LineaUtilities;
import devs.mrp.gullproject.validator.AttributeValueValidator;
import devs.mrp.gullproject.validator.ValidList;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping(path = "/lineas")
public class LineaController {

	private LineaService lineaService;
	private ConsultaService consultaService;
	private ModelMapper modelMapper;
	AtributoServiceProxyWebClient atributoService;
	LineaUtilities lineaUtilities;

	@Autowired
	public LineaController(LineaService lineaService, ConsultaService consultaService, ModelMapper modelMapper,
			AtributoServiceProxyWebClient atributoService, LineaUtilities lineaUtilities) {
		this.lineaService = lineaService;
		this.consultaService = consultaService;
		this.modelMapper = modelMapper;
		this.atributoService = atributoService;
		this.lineaUtilities = lineaUtilities;
	}

	@GetMapping("/allof/propid/{propuestaId}")
	public String showAllLinesOf(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Flux<Linea> lineas = lineaService.findByPropuestaId(propuestaId);
		model.addAttribute("lineas", new ReactiveDataDriverContextVariable(lineas, 1));
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "showAllLineasOfPropuesta";
	}
	
	@GetMapping("/allof/propid/{propuestaId}/order")
	public String orderAllLinesOf(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<WrapLineasDto> lineas = lineaService.findByPropuestaId(propuestaId)
				.collectList().flatMap(rList -> {
					WrapLineasDto wrap = new WrapLineasDto();
					wrap.setLineas(rList);
					return Mono.just(wrap);
				});
		model.addAttribute("wrapLineasDto", lineas);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "orderLineasOfPropuesta";
	}
	
	@PostMapping("/allof/propid/{propuestaId}/order")
	public String processOrderAllLinesOf(WrapLineasDto wrapLineasDto, Model model, @PathVariable(name ="propuestaId") String propuestaId) {
		Map<String, Integer> map = new HashMap<>();
		wrapLineasDto.getLineas().stream().forEach(sLinea -> {
			map.put(sLinea.getId(), sLinea.getOrder());
		});
		Mono<Void> mono = lineaService.updateOrderOfSeveralLineas(map);
		model.addAttribute("orderMap", mono);
		model.addAttribute("propuestaId", propuestaId);
		return "processOrderLineasOfPropuesta";
	}

	@GetMapping("/of/{propuestaId}/new")
	public String addLineToPropuesta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		Linea lLinea = new Linea();
		lLinea.setPropuestaId(propuestaId);
		Mono<LineaWithAttListDto> atributosDePropuesta = lineaUtilities.getAttributesOfProposal(lLinea, propuestaId);
		model.addAttribute("propuesta", propuesta);
		model.addAttribute("propuestaId", propuestaId);
		model.addAttribute("lineaWithAttListDto", atributosDePropuesta);
		return "addLineaToPropuesta";
	}

	@PostMapping("/of/{propuestaId}/new") // TODO campo para "cuantas quieres añadir iguales?"
	public Mono<String> processAddLineaToPropuesta(@Valid LineaWithAttListDto lineaWithAttListDto,
			BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		return lineaUtilities.assertBindingResultOfListDto(lineaWithAttListDto, bindingResult).then(Mono.just(bindingResult))
				.flatMap(rBindingResult -> {
					if (rBindingResult.hasErrors()) {
						model.addAttribute("propuesta", consultaService.findPropuestaByPropuestaId(propuestaId));
						model.addAttribute("propuestaId", propuestaId);
						model.addAttribute("lineaWithAttListDto", lineaWithAttListDto);
						return Mono.just("addLineaToPropuesta");
					} else {
						Mono<Linea> l1;
						Mono<Propuesta> p1;
						if (lineaWithAttListDto.getLinea().getPropuestaId().equals(propuestaId)) {
							log.debug("propuestaId's equal");
							Mono<Linea> llinea = lineaUtilities.reconstructLine(lineaWithAttListDto);
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
	
	@GetMapping("/of/{propuestaId}/bulk-add") // TODO test
	public String bulkAddLineasToPropuesta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		model.addAttribute("propuesta", propuesta);
		model.addAttribute("propuestaId", propuestaId);
		StringWrapper wrap = new StringWrapper("");
		model.addAttribute("stringWrapper", wrap);
		return "bulkAddLineastoPropuesta";
	}
	
	@PostMapping("/of/{propuestaId}/bulk-add") // TODO test
	public String processBulkAddLineastoPropuesta(StringWrapper stringWrapper, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		String texto = stringWrapper.getString();
		if (texto == null || texto.equals("")) {
			bindingResult.rejectValue("string", "error.stringWrapper.string", "Debes introducir un texto");
		}
		if (bindingResult.hasErrors()) {
			Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
			model.addAttribute("propuesta", propuesta);
			model.addAttribute("propuestaId", propuestaId);
			model.addAttribute("stringWrapper", stringWrapper);
			return "bulkAddLineastoPropuesta";
		}
		
		model.addAttribute("stringListOfListsWrapper", lineaUtilities.excelTextToLineObject(texto)); // TODO probar primera y útlima fila en blanco, por tema del split
		
		model.addAttribute("atributos", consultaService.findAttributesByPropuestaId(propuestaId));
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		model.addAttribute("propuesta", propuesta);
		model.addAttribute("propuestaId", propuestaId);
		
		return "processBulkAddLineasToPropuesta";
	}
	
	@PostMapping("/of/{propuestaId}/bulk-add/verify") // TODO test
	public Mono<String> verifyBulkAddLineastoPropuesta(StringListOfListsWrapper stringListOfListsWrapper, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		// verify that the data for each column is appropiate according to the attribute
		String nameIdentifier = "name";
		try {
			return lineaUtilities.addBulkTableErrorsToBindingResult(stringListOfListsWrapper, propuestaId, bindingResult, nameIdentifier)
				.flatMap(v -> {
					if (bindingResult.hasErrors()) {
						model.addAttribute("stringListOfListsWrapper", stringListOfListsWrapper);
						model.addAttribute("atributos", consultaService.findAttributesByPropuestaId(propuestaId));
						Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
						model.addAttribute("propuesta", propuesta);
						model.addAttribute("propuestaId", propuestaId);
						return Mono.just("processBulkAddLineasToPropuesta");
					} else {
						try {
							return lineaUtilities.allLineasFromBulkWrapper(stringListOfListsWrapper, propuestaId)
									.flatMapMany(rAllLineas -> lineaService.addVariasLineas(Flux.fromIterable(rAllLineas), propuestaId))
										.then(Mono.just("verifyBulkAddLineasToPropuesta"));
						} catch (Exception e) {
							return Mono.just("processBulkAddLineasToPropuestaError");
						}
					}
				});
		} catch (Exception e) {
			return Mono.just("processBulkAddLineasToPropuestaError");
		}
	}

	@GetMapping("/revisar/id/{lineaid}")
	public String revisarLinea(Model model, @PathVariable(name = "lineaid") String lineaId) {
		Mono<Linea> linea = lineaService.findById(lineaId);
		Mono<LineaWithAttListDto> lineaDto = lineaUtilities.getAttributesOfProposal(linea);

		model.addAttribute("lineaWithAttListDto", lineaDto);

		return "reviewLinea";
	}

	@PostMapping("/revisar/id/{lineaid}")
	public Mono<String> processRevisarLinea(@Valid LineaWithAttListDto lineaWithAttListDto, BindingResult bindingResult,
			Model model, @PathVariable(name = "lineaid") String lineaId) {
		return lineaUtilities.assertBindingResultOfListDto(lineaWithAttListDto, bindingResult).then(Mono.just(bindingResult))
				.flatMap(rBindingResult -> {
					if (rBindingResult.hasErrors()) {
						model.addAttribute("lineaWithAttListDto", lineaWithAttListDto);
						return Mono.just("reviewLinea");
					} else {
						Mono<Linea> l1;
						if (lineaWithAttListDto.getLinea().getId().equals(lineaId)) {
							log.debug("propuestaId's equal");
							Mono<Linea> llinea = lineaUtilities.reconstructLine(lineaWithAttListDto);
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
	public String deleteLinea(Model model, @PathVariable(name = "lineaid") String lineaId) {
		Mono<Linea> linea = lineaService.findById(lineaId);
		model.addAttribute("linea", linea);
		return "deleteLinea";
	}

	@PostMapping("/delete/id/{lineaid}")
	public String processDeleteLinea(Linea linea, BindingResult bindingResult, Model model,
			@PathVariable(name = "lineaid") String lineaId) {
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

	@GetMapping("/deleteof/propid/{propuestaId}")
	public String deleteLinesOf(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<WrapLineasWithSelectorDto> lineas = lineaService.findByPropuestaId(propuestaId).map(rl -> {
			LineaWithSelectorDto dto = modelMapper.map(rl, LineaWithSelectorDto.class);
			dto.setSelected(false);
			return dto;
		}).collectList().flatMap(rList -> {
			WrapLineasWithSelectorDto wrap = new WrapLineasWithSelectorDto();
			wrap.setLineas(rList);
			return Mono.just(wrap);
		});
		model.addAttribute("wrapLineasWithSelectorDto", lineas);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "deleteLinesOf";
	}

	@PostMapping("/deleteof/propid/{propuestaId}")
	public String processDeleteLinesOf(WrapLineasWithSelectorDto wrapLineasWithSelectorDto, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("wrapLineasWithSelectorDto", wrapLineasWithSelectorDto);
			Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
			model.addAttribute("consulta", consulta);
			model.addAttribute("propuestaId", propuestaId);
			return "deleteLinesOf";
		}
		List<LineaWithSelectorDto> lineas = wrapLineasWithSelectorDto.getLineas();
		Iterator<LineaWithSelectorDto> iterator = lineas.iterator();
		while (iterator.hasNext()) {
			LineaWithSelectorDto dto = iterator.next();
			if (!dto.getSelected()) {
				iterator.remove();
			}
		}
		model.addAttribute("wrapLineasWithSelectorDto", wrapLineasWithSelectorDto);
		model.addAttribute("propuestaId", propuestaId);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		return "processDeleteLinesOf";
	}

	@PostMapping("/deleteof/propid/{propuestaId}/confirmed")
	public String processConfirmDeleteLinesOf(WrapLineasWithSelectorDto wrapLineasWithSelectorDto, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<Void> delete = lineaService
				.deleteVariasLineas(Flux.fromIterable(wrapLineasWithSelectorDto.getLineas()).map(rLineaDto -> {
					Linea linea = modelMapper.map(rLineaDto, Linea.class);
					return linea;
				}));
		model.addAttribute("delete", delete);
		return "confirmDeleteLinesOf";
	}	

}
