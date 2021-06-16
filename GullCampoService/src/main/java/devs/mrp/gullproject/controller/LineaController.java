package devs.mrp.gullproject.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.bson.types.ObjectId;
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
import devs.mrp.gullproject.domains.TipoPropuesta;
import devs.mrp.gullproject.domains.WrapLineasDto;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AttributesListDto;
import devs.mrp.gullproject.domains.dto.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.AttRemaper;
import devs.mrp.gullproject.domains.dto.AttRemapersWrapper;
import devs.mrp.gullproject.domains.dto.LineaWithAttListDto;
import devs.mrp.gullproject.domains.dto.LineaWithSelectorDto;
import devs.mrp.gullproject.domains.dto.MultipleLineaWithAttListDto;
import devs.mrp.gullproject.domains.dto.WrapLineasWithSelectorDto;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.AttRemaperUtilities;
import devs.mrp.gullproject.service.ClassDestringfier;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaOperations;
import devs.mrp.gullproject.service.LineaService;
import devs.mrp.gullproject.service.LineaUtilities;
import devs.mrp.gullproject.validator.AttributeValueValidator;
import devs.mrp.gullproject.validator.ValidList;
import devs.mrp.gullproject.domains.PropuestaProveedor;
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
	AttRemaperUtilities attRemaperUtilities;

	@Autowired
	public LineaController(LineaService lineaService, ConsultaService consultaService, ModelMapper modelMapper,
			AtributoServiceProxyWebClient atributoService, LineaUtilities lineaUtilities, AttRemaperUtilities attRemaperUtilities) {
		this.lineaService = lineaService;
		this.consultaService = consultaService;
		this.modelMapper = modelMapper;
		this.atributoService = atributoService;
		this.lineaUtilities = lineaUtilities;
		this.attRemaperUtilities = attRemaperUtilities;
	}
	
	// TODO for excels where one sheet = one product, first map fields in the sheet row/column=attribute, then extract data from several sheets following this map.

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
		Mono<WrapLineasDto> lineas = lineaUtilities.wrapLineasDtoFromPropuestaId(propuestaId);
		model.addAttribute("wrapLineasDto", lineas);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "orderLineasOfPropuesta";
	}
	
	@PostMapping("/allof/propid/{propuestaId}/order")
	public String processOrderAllLinesOf(WrapLineasDto wrapLineasDto, Model model, @PathVariable(name ="propuestaId") String propuestaId) {
		Map<String, Integer> map = lineaUtilities.linesWrapToMapOf_Id_vs_Order(wrapLineasDto);
		Mono<Void> mono = lineaService.updateOrderOfSeveralLineas(map);
		model.addAttribute("orderMap", mono);
		model.addAttribute("propuestaId", propuestaId);
		return "processOrderLineasOfPropuesta";
	}
	
	@GetMapping("/allof/propid/{propuestaId}/rename")
	public String renameAllLinesOf(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<StringListOfListsWrapper> lineas = lineaUtilities.stringListOfListsFromPropuestaId(propuestaId);
		model.addAttribute("stringListOfListsWrapper", lineas);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "renameAllLinesOf";
	}
	
	@PostMapping("/allof/propid/{propuestaId}/rename")
	public Mono<String> processRenameAllLinesOf(StringListOfListsWrapper stringListOfListsWrapper, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		
		model.addAttribute("stringListOfListsWrapper", stringListOfListsWrapper);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		
		try {
			lineaUtilities.addBulkTableErrorsToBindingResult(stringListOfListsWrapper, propuestaId, bindingResult);
		} catch (Exception e) {
			e.printStackTrace();
			return Mono.just("renameAllLinesOf");
		}
		
		if (bindingResult.hasErrors()) {
			return Mono.just("renameAllLinesOf");
		}
		
		return lineaUtilities.updateNombresFromStringListOfListsWrapper(stringListOfListsWrapper)
				.then(Mono.just("processRenameAllLinesOf"));
	}
	
	@GetMapping("/allof/propid/{propuestaId}/remap")
	public String remapValuesGeneral(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Flux<Linea> lineas = lineaService.findByPropuestaId(propuestaId);
		model.addAttribute("lineas", new ReactiveDataDriverContextVariable(lineas, 1));
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "remapValuesGeneral";
	}
	
	@GetMapping("/allof/propid/{propuestaId}/remap/{localIdentifier}")
	public String remapValuesAttColumn(Model model, @PathVariable(name = "propuestaId") String propuestaId, @PathVariable(name = "localIdentifier") String localIdentifier) {
		Mono<AttRemapersWrapper> remapers = lineaUtilities.getRemappersFromPropuestaAndAttId(propuestaId, localIdentifier);
		model.addAttribute("attRemapersWrapper", remapers);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "remapValuesAttColumn";
	}
	
	@PostMapping("/allof/propid/{propuestaId}/remap/{localIdentifier}")
	public Mono<String> processRemapValuesAttColumn(AttRemapersWrapper attRemapersWrapper, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId, @PathVariable(name = "localIdentifier") String localIdentifier) {
		model.addAttribute("attRemapersWrapper", attRemapersWrapper);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		
		return attRemaperUtilities.validateAttRemapers(attRemapersWrapper.getRemapers(), bindingResult, "remapers")
				.then(Mono.just(attRemapersWrapper))
					.flatMap(wrapper -> {
						if (bindingResult.hasErrors()) {
							return Mono.just("remapValuesAttColumn");
						} else {
							return attRemaperUtilities.remapLineasAtt(attRemapersWrapper.getRemapers(), propuestaId)
								.then(Mono.just("processRemapValuesAttcolumn"));
						}
					});
	}
	
	@GetMapping("/allof/propid/{propuestaId}/edit")
	public String editAllLinesOf(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<MultipleLineaWithAttListDto> lineaDtos = lineaUtilities.getWrappedLineasWithAttListDtoFromPropuestaId(propuestaId);
		model.addAttribute("multipleLineaWithAttListDto", lineaDtos);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "editAllLinesOf";
	}
	
	@PostMapping("/allof/propid/{propuestaId}/edit")
	public Mono<String> processEditAllLinesOf(MultipleLineaWithAttListDto multipleLineaWithAttListDto, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return lineaUtilities.assertBindingResultOfWrappedMultipleLines(multipleLineaWithAttListDto, bindingResult)
				.collectList()
				.flatMap(rDtoList -> {
					if(bindingResult.hasErrors()) {
						log.debug("bindingResult tiene errores, enviamos de vuelta: " + multipleLineaWithAttListDto.toString());
						model.addAttribute("multipleLineaWithAttListDto", multipleLineaWithAttListDto);
						return Mono.just("editAllLinesOf");
					} else {
						return lineaUtilities.updateLinesFromListOfLinesWithAttListDto(rDtoList, propuestaId)
								.collectList().map(rListDtos -> {
									MultipleLineaWithAttListDto multiple = lineaUtilities.wrapLinesIntoMultipleObject(rListDtos);
									model.addAttribute("multipleLineaWithAttListDto", multiple);
									return multiple;
								})
								.then(Mono.just("processEditAllLinesOf"));
					}
				});
	}

	@GetMapping("/of/{propuestaId}/new")
	public String addLineToPropuesta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Linea lLinea = new Linea();
		lLinea.setPropuestaId(propuestaId);
		Mono<LineaWithAttListDto> atributosDePropuesta = lineaUtilities.getAttributesOfProposal(lLinea, propuestaId, 1);
		model.addAttribute("propuestaId", propuestaId);
		model.addAttribute("lineaWithAttListDto", atributosDePropuesta);
		
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		model.addAttribute("propuesta", propuesta);
		log.debug("added Propuesta");
		return "addLineaToPropuesta";
	}

	@PostMapping("/of/{propuestaId}/new")
	public Mono<String> processAddLineaToPropuesta(@Valid LineaWithAttListDto lineaWithAttListDto, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		return lineaUtilities.assertBindingResultOfListDto(lineaWithAttListDto, bindingResult, "attributes")
				.then(Mono.just(bindingResult))
				.flatMap(rBindingResult -> {
					if (rBindingResult.hasErrors()) {
						model.addAttribute("propuesta", consultaService.findPropuestaByPropuestaId(propuestaId));
						model.addAttribute("propuestaId", propuestaId);
						model.addAttribute("lineaWithAttListDto", lineaWithAttListDto);
						return Mono.just("addLineaToPropuesta");
					} else {
						Flux<Linea> l1;
						Mono<Propuesta> p1;
						p1 = consultaService.findPropuestaByPropuestaId(propuestaId);
						if (lineaWithAttListDto.getLinea().getPropuestaId().equals(propuestaId)) {
							log.debug("propuestaId's equal");
							l1 = lineaUtilities.addSeveralCopiesOfSameLineDto(lineaWithAttListDto, propuestaId);
						} else {
							log.debug("propuestaId's are NOT equal");
							l1 = Flux.empty();
						}
						model.addAttribute("lineas", new ReactiveDataDriverContextVariable(l1, 1));
						model.addAttribute("propuesta", p1);
						model.addAttribute("qty", lineaWithAttListDto.getQty());
						return Mono.just("processAddLineaToPropuesta");
					}
				});
	}
	
	@GetMapping("/of/{propuestaId}/bulk-add")
	public String bulkAddLineasToPropuesta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		model.addAttribute("propuesta", propuesta);
		model.addAttribute("propuestaId", propuestaId);
		StringWrapper wrap = new StringWrapper("");
		model.addAttribute("stringWrapper", wrap);
		return "bulkAddLineastoPropuesta";
	}
	
	@PostMapping("/of/{propuestaId}/bulk-add") // TODO test costes
	public Mono<String> processBulkAddLineastoPropuesta(StringWrapper stringWrapper, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		String texto = stringWrapper.getString();
		if (texto == null || texto.equals("")) {
			bindingResult.rejectValue("string", "error.stringWrapper.string", "Debes introducir un texto");
		}
		if (bindingResult.hasErrors()) {
			Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
			model.addAttribute("propuesta", propuesta);
			model.addAttribute("propuestaId", propuestaId);
			model.addAttribute("stringWrapper", stringWrapper);
			return Mono.just("bulkAddLineastoPropuesta");
		}
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		return propuesta.map(rPro -> {
			model.addAttribute("stringListOfListsWrapper", lineaUtilities.excelTextToLineObject(texto));
			model.addAttribute("atributos", rPro.getAttributeColumns());
			if (rPro instanceof PropuestaProveedor) {
				model.addAttribute("costes", ((PropuestaProveedor)rPro).getCostes());
			}
			model.addAttribute("propuesta", rPro);
			model.addAttribute("propuestaId", rPro.getId());
			return "processBulkAddLineasToPropuesta";
		});
		
		/*model.addAttribute("stringListOfListsWrapper", lineaUtilities.excelTextToLineObject(texto));
		model.addAttribute("atributos", consultaService.findAttributesByPropuestaId(propuestaId));
		model.addAttribute("propuesta", propuesta);
		model.addAttribute("propuestaId", propuestaId);
		
		return "processBulkAddLineasToPropuesta";*/
	}
	
	@PostMapping("/of/{propuestaId}/bulk-add/verify") // TODO test costes
	public Mono<String> verifyBulkAddLineastoPropuesta(StringListOfListsWrapper stringListOfListsWrapper, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		// verify that the data for each column is appropiate according to the attribute
		model.addAttribute("atributos", consultaService.findAttributesByPropuestaId(propuestaId));
		Mono<Propuesta> propuesta = consultaService.findPropuestaByPropuestaId(propuestaId);
		model.addAttribute("propuesta", propuesta);
		model.addAttribute("propuestaId", propuestaId);
		try {
			log.debug("going to find errors");
			return lineaUtilities.addBulkTableErrorsToBindingResult(stringListOfListsWrapper, propuestaId, bindingResult)
				.then(Mono.just(bindingResult))
				.flatMap(binding -> {
					log.debug("to choose between if bindingResult has errors or not");
					if (binding.hasErrors()) {
						log.debug("binding result has errors and we go back to the same view highlighting errors");
						log.debug("adding attribute stringListOfListsWrapper: " + stringListOfListsWrapper.toString());
						model.addAttribute("stringListOfListsWrapper", stringListOfListsWrapper);
						return propuesta.map(rPro -> {
							if (rPro instanceof PropuestaProveedor) {
								model.addAttribute("costes", ((PropuestaProveedor)rPro).getCostes());
							}
							return "processBulkAddLineasToPropuesta";
						});
					} else {
						try {
							log.debug("binding result has no errors");
							log.debug("going to start getting all lineas from bulk");
							return lineaUtilities.allLineasFromBulkWrapper(stringListOfListsWrapper, propuestaId)
									.flatMapMany(rAllLineas -> {
												log.debug("esto es lo que se va a pasar a la db");
												log.debug(rAllLineas.toString());
												rAllLineas.stream().forEach(l -> log.debug(l.toString()));
												return lineaService.addVariasLineas(Flux.fromIterable(rAllLineas), propuestaId);
											}).collectList().map(rLineasInserted -> {
												model.addAttribute("lineas", rLineasInserted);
												return rLineasInserted;
											})
										.then(Mono.just("verifyBulkAddLineasToPropuesta").map(mon -> {
											log.debug("y devolvemos la pantalla siguiente de verificación");
											return mon;
										}));
						} catch (Exception e) {
							log.debug("exception during verification and we go back to the same view");
							e.printStackTrace();
							return Mono.just("processBulkAddLineasToPropuesta");
						}
					}
				});
		} catch (Exception e) {
			log.debug("exception during add errors to bindingresult and we go back to the same view");
			e.printStackTrace();
			model.addAttribute("stringListOfListsWrapper", stringListOfListsWrapper);
			return Mono.just("processBulkAddLineasToPropuesta");
		}
	}

	@GetMapping("/revisar/id/{lineaid}")
	public Mono<String> revisarLinea(Model model, @PathVariable(name = "lineaid") String lineaId) {
		Mono<Linea> linea = lineaService.findById(lineaId);
		Mono<LineaWithAttListDto> lineaDto = lineaUtilities.getAttributesOfProposal(linea, 1);

		model.addAttribute("lineaWithAttListDto", lineaDto);

		return linea.map(l -> consultaService.findPropuestaByPropuestaId(l.getPropuestaId()))
				.map(prop -> {
					model.addAttribute("propuesta", prop);
					return "reviewLinea";
				});
	}

	@PostMapping("/revisar/id/{lineaid}")
	public Mono<String> processRevisarLinea(@Valid LineaWithAttListDto lineaWithAttListDto, BindingResult bindingResult,
			Model model, @PathVariable(name = "lineaid") String lineaId) {
		return lineaUtilities.assertBindingResultOfListDto(lineaWithAttListDto, bindingResult, "attributes").then(Mono.just(bindingResult))
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
		Mono<WrapLineasWithSelectorDto> lineas = lineaUtilities.getWrappedLinesWithSelectorFromPropuestaId(propuestaId);
		model.addAttribute("wrapLineasWithSelectorDto", lineas);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("propuestaId", propuestaId);
		return "deleteLinesOf";
	}

	@PostMapping("/deleteof/propid/{propuestaId}")
	public String processDeleteLinesOf(WrapLineasWithSelectorDto wrapLineasWithSelectorDto, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		model.addAttribute("propuestaId", propuestaId);
		Mono<Consulta> consulta = consultaService.findConsultaByPropuestaId(propuestaId);
		model.addAttribute("consulta", consulta);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("wrapLineasWithSelectorDto", wrapLineasWithSelectorDto);
			return "deleteLinesOf";
		}
		lineaUtilities.removeNotSelectedFromWrap(wrapLineasWithSelectorDto);
		model.addAttribute("wrapLineasWithSelectorDto", wrapLineasWithSelectorDto);
		return "processDeleteLinesOf";
	}

	@PostMapping("/deleteof/propid/{propuestaId}/confirmed")
	public String processConfirmDeleteLinesOf(WrapLineasWithSelectorDto wrapLineasWithSelectorDto, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		Mono<Void> delete = lineaUtilities.deleteSelectedLinesFromWrap(wrapLineasWithSelectorDto);
		model.addAttribute("delete", delete);
		return "confirmDeleteLinesOf";
	}	

}
