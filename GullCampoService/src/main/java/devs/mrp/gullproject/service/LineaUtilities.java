package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.StringListOfListsWrapper;
import devs.mrp.gullproject.domains.StringListWrapper;
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.LineaWithAttListDto;
import devs.mrp.gullproject.validator.ValidList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Data
@Service
public class LineaUtilities {
	
	ConsultaService consultaService;
	AtributoServiceProxyWebClient atributoService;
	ModelMapper modelMapper;
	
	public LineaUtilities(ConsultaService consultaService, ModelMapper modelMapper, AtributoServiceProxyWebClient atributoService) {
		this.consultaService = consultaService;
		this.modelMapper = modelMapper;
		this.atributoService = atributoService;
	}

	public Mono<LineaWithAttListDto> getAttributesOfProposal(Linea lLinea, String propuestaId) {
		return consultaService.findAttributesByPropuestaId(propuestaId)
				.map(rAttProp -> modelMapper.map(rAttProp, AtributoForLineaFormDto.class)).map(rAttForForm -> {
					rAttForForm.setValue(lLinea.getValueByAttId(rAttForForm.getId()));
					return rAttForForm;
				}).collectList().flatMap(rAttFormList -> Mono
						.just(new LineaWithAttListDto(lLinea, new ValidList<AtributoForLineaFormDto>(rAttFormList))));
	}

	public Mono<LineaWithAttListDto> getAttributesOfProposal(Mono<Linea> lLinea) {
		return lLinea.flatMap(linea -> getAttributesOfProposal(linea, linea.getPropuestaId()));
	}

	public Flux<Boolean> assertBindingResultOfListDto(LineaWithAttListDto lineaWithAttListDto,
			BindingResult bindingResult) {
		/**
		 * BindingResult checks out of the box if there is any error in the line, but
		 * not in the attributes (we removed the validation in that class) To check if
		 * the attributes are correct we need to call the attribute repository, which
		 * returns a Reactor object The class AttributeValueValidator.class can validate
		 * the result, but it needs to block the reactor object in a flux thread So we
		 * need to add errors manually to the bindingResult in a flow and return a Mono
		 * to avoid blocking
		 */
		if (lineaWithAttListDto.getAttributes() == null || lineaWithAttListDto.getAttributes().size() == 0) {
			return Flux.just(true);
		}
		Map<AtributoForLineaFormDto, Integer> map = new HashMap<>();
		return Mono.just(lineaWithAttListDto.getAttributes()).map(rAttList -> {
			for (int i = 0; i < rAttList.size(); i++) {
				map.put(rAttList.get(i), i);
			}
			return rAttList;
		}).flatMapMany(rAttList -> Flux.fromIterable(rAttList)).flatMap(rAtt -> {
			if (!rAtt.getValue().isBlank()) {
				log.debug("att type: " + rAtt.getTipo());
				log.debug("at value: " + rAtt.getValue());
				return atributoService.validateDataFormat(rAtt.getTipo(), rAtt.getValue().replace(",", ".")) // we replace "," with "." in case it is a decimal number, as in Europe "," is used
						.map(rBool -> {
							if (!rBool) {
								Integer pos = map.get(rAtt);
								bindingResult.rejectValue("attributes[" + pos + "].id",
										"error.atributosDeLinea.attributes[" + pos + "]",
										"El valor no es correcto para este atributo.");
							}
							return rBool;
						});
			} else {
				return Mono.just(true);
			}
		});
	}

	public Mono<Linea> reconstructLine(LineaWithAttListDto lineaWithAttListDto) {
		Linea nLinea = lineaWithAttListDto.getLinea();
		List<AtributoForLineaFormDto> nAtts = lineaWithAttListDto.getAttributes();
		if (nAtts == null) {
			nAtts = new ValidList<>();
		}
		return Flux.fromIterable(nAtts)
				.flatMap(rAtt -> atributoService.getClassTypeOfFormat(rAtt.getTipo()).map(
						rClass -> new Campo<Object>(rAtt.getId(), ClassDestringfier.toObject(rClass, rAtt.getValue()))))
				.collectList().map(rCampoList -> {
					nLinea.replaceOrAddCamposObj(rCampoList);
					return nLinea;
				});
	}
	
	public StringListOfListsWrapper excelTextToLineObject(String texto) {
		// arrange the lines in an object that can be used in Thymeleaf Template
		String lines[] = texto.split(System.lineSeparator());
		Integer nOfCols = 0;
		StringListOfListsWrapper fieldArrays = new StringListOfListsWrapper();
		for (int i = 0; i<lines.length; i++) {
			List<String> fl = Arrays.asList(lines[i].split("\\t"));
			fieldArrays.add(new StringListWrapper(fl));
			if (fl.size() > nOfCols) {
				nOfCols = fl.size();
			}
		}
		
		for (int i = 0; i<nOfCols; i++) {
			// it is a bit dumb but is the best way to match the fields in thymeleaf and get the selected value back
			fieldArrays.add("");
		}
		
		return fieldArrays;
	}
	
	/**
	 * 
	 * below...
	 * For Validating a List of Lists from Bulk data of excel-paste
	 * 
	 * 
	 */
	
	public Mono<Boolean[][]> ifListOfListsHasValidData(StringListOfListsWrapper filas, String propuestaId, BindingResult bindingResult) { // TODO test
		// Use indexed(), index()
		// https://github.com/reactor/reactor-core/issues/1041
		// https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#index--
		int i = filas.getStringListWrapper().size();
		int j = filas.getStrings().size();
		Boolean[][] validations = new Boolean[i][j];
		mapOfAttIdsToTipo(propuestaId)
			.map(rAttToTipo -> {
				for (int x=0; x<i; x++) {
					for (int y=0; y<j; y++) {
						
					}
				}
				return 0;
			});
	}
	
	private Mono<Map<String, String>> mapOfAttIdsToTipo(String propuestaId) {
		return consultaService.findAttributesByPropuestaId(propuestaId)
			.collectMap(rAtt -> rAtt.getId(), rAtt -> rAtt.getTipo());
	}
	
	private Map<String, String> mapOfAttIdToData(List<String> columns, List<String> fila) throws Exception {
		if (columns.size() != fila.size()) {
			throw new Exception("listas deben ser del mismo tamaño");
		}
		Map<String, String> map = new HashMap<>();
		for (int i=0; i<columns.size(); i++) {
			map.put(columns.get(i), fila.get(i));
		}
		return map;
	}
	
	private Map<StringListWrapper, Integer> mapOfFilaToPosicion(StringListOfListsWrapper filas) {
		Map<StringListWrapper, Integer> map = new HashMap<>();
		List<StringListWrapper> mf = filas.getStringListWrapper();
		for (int i=0; i<mf.size(); i++) {
			map.put(mf.get(i), i);
		}
		return map;
	}
	
	private Map<String, Integer> mapOfAttIdToPosicionColumna(StringListOfListsWrapper filas) {
		List<String> columnas = filas.getStrings();
		Map<String, Integer> map = new HashMap<>();
		for (int i=0; i<columnas.size();i++) {
			map.put(columnas.get(i), i);
		}
		return map;
	}
	
}
