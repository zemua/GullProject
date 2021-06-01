package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
import devs.mrp.gullproject.domains.dto.BooleanWrapper;
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
	 * @throws Exception 
	 * 
	 * 
	 */
	
	public Mono<Void> addBulkTableErrorsToBindingResult(StringListOfListsWrapper wrapper, String propuestaId, BindingResult bindingResult, String nameIdentifier) throws Exception {
		return bulkTableWrapperToTuplaTabla(wrapper, propuestaId, nameIdentifier)
				.map(rTupla -> {
					if (!rTupla.validado) {
						// reject the field
						bindingResult.rejectValue("stringListWrapper[" + rTupla.fila + "].string[" + rTupla.columna + "]",
								"error.stringListOfListsWrapper.stringListWrapper[" + rTupla.fila + "].string",
								"El valor no es correcto para este atributo.");
						bindingResult.rejectValue("strings[" + rTupla.columna + "]",
								"error.stringListOfListsWrapper.strings",
								"El valor no es correcto para este atributo.");
					}
					return rTupla.validado;
				})
				.then(Mono.empty());
	}
	
	private class TuplaTabla {
		Integer fila;
		Integer columna;
		String attId;
		String tipo;
		String valor;
		Boolean validado;
	}
	
	private Flux<TuplaTabla> bulkTableWrapperToTuplaTabla(StringListOfListsWrapper wrapper, String propuestaId, String nameIdentifier) throws Exception { // TODO test
		List<TuplaTabla> tuplas = mapToTuplaTabla(wrapper);
		AtomicInteger counter = new AtomicInteger();
		counter.set(0);
		
		return mapOfAttIdsToTipo(propuestaId)
			.flatMapMany(rAttToTipo -> {
				return Flux.fromIterable(tuplas)
						.flatMap(fTupla -> {
							fTupla.tipo = rAttToTipo.get(fTupla.attId);
							if (fTupla.attId.equals(nameIdentifier)) { // This is the column corresponding to the line's name in the table, not an attribute
								fTupla.validado = true;
								if (fTupla.valor.isBlank()) {
									fTupla.valor = String.valueOf(counter.incrementAndGet()); // line's name should not be empty
								}
								return Mono.just(fTupla);
							} else {
							return atributoService.validateDataFormat(fTupla.tipo, fTupla.valor)
									.map(rBool -> {
										fTupla.validado = rBool;
										return fTupla;
									});
							}
						});
			});
	}
	
	private Mono<Map<String, String>> mapOfAttIdsToTipo(String propuestaId) {
		return consultaService.findAttributesByPropuestaId(propuestaId)
			.collectMap(rAtt -> rAtt.getId(), rAtt -> rAtt.getTipo());
	}
	
	private List<TuplaTabla> mapToTuplaTabla(StringListOfListsWrapper wrapper) throws Exception {
		List<TuplaTabla> tuplas = new ArrayList<>();
		
		List<StringListWrapper> lineas = wrapper.getStringListWrapper();
		List<String> columnas = wrapper.getStrings();
		
		for (int i=0; i<lineas.size(); i++) {
			for (int j=0; j<columnas.size(); j++) {
				if (columnas.size() != lineas.size()) {
					throw new Exception("incorrect length of lines");
				}
				TuplaTabla tu = new TuplaTabla();
				tu.fila = i;
				tu.columna = j;
				tu.attId = columnas.get(j);
				tu.valor = lineas.get(i).get(j);
				// remains the type and the validation, will be done in a flux together
				tuplas.add(tu);
			}
		}
		
		return tuplas;
	}
	
	
	/**
	 * 
	 * 
	 * Convert bulk wrapper to lines to insert into the db
	 * 
	 * 
	 */
	
	
	
	private class DuplaAttVal {
		String attId;
		String tipo;
		String clase;
		String valor;
	}
	
	public Mono<List<Linea>> allLineasFromBulkWrapper(StringListOfListsWrapper wrapper, String propuestaId) throws Exception { // TODO test
		return allLineasInDuplaCompleta(wrapper, propuestaId)
				.map(rAllDuplas -> {
					List<Linea> listOfLineas = new ArrayList<>();
					
					rAllDuplas.stream().forEach(sDupla ->{
						Linea linea = new Linea();
						sDupla.stream().forEach(sField -> {
							Campo<Object> campo = new Campo<>();
							campo.setAtributoId(sField.attId);
							campo.setDatos(ClassDestringfier.toObject(sField.clase, sField.valor));
							linea.addCampo(campo);
						});
						listOfLineas.add(linea);
					});
					
					return listOfLineas;
				});
	}
	
	private Mono<List<List<DuplaAttVal>>> allLineasInDuplaCompleta(StringListOfListsWrapper wrapper, String propuestaId) throws Exception {
		List<List<DuplaAttVal>> duplas = allLineasInDuplaWithAttidAndValor(wrapper);
		return mapOfAttIdsToTipo(propuestaId)
			.flatMap(rAttIdToTipo -> {
				
				Map<String, String> attIdToClass = new HashMap<>();
				
				return Flux.fromIterable(rAttIdToTipo.keySet())
						// first we map the classes vs ids so we just need to make one query per att to the db
					.map(rAttId -> atributoService.getClassTypeOfFormat(rAttIdToTipo.get(rAttId))
								.map(rClass -> {
									attIdToClass.put(rAttId, rClass);
									return rClass;
								}))
					
					// then we use the map to fill the remaining data
					.then(Mono.just(duplas).map(rDuplas -> {
						
						rDuplas.forEach(rLinea -> {
							rLinea.forEach(rCampo -> {
								rCampo.tipo = rAttIdToTipo.get(rCampo.attId);
								rCampo.clase = attIdToClass.get(rCampo.attId);
							});
						});
						
						return rDuplas;
					}));
			});
	}
	
	private List<List<DuplaAttVal>> allLineasInDuplaWithAttidAndValor(StringListOfListsWrapper wrapper) throws Exception {
		List<List<DuplaAttVal>> lista = new ArrayList<>();
		for (int i =0; i<wrapper.getStrings().size(); i++) {
			lista.add(lineaDuplaWithAttidAndValor(wrapper, i));
		}
		return lista;
	}
	
	private List<DuplaAttVal> lineaDuplaWithAttidAndValor(StringListOfListsWrapper wrapper, int lineN) throws Exception {
		List<String> columnas = wrapper.getStrings();
		List<DuplaAttVal> linea = new ArrayList<>();
		StringListWrapper current = wrapper.getStringListWrapper().get(lineN);
		
		if (current.getString().size() != columnas.size()) {
			throw new Exception("error on line length");
		}
		
		for (int i=0; i<columnas.size(); i++) {
			if (!columnas.get(i).isBlank()) {
				DuplaAttVal dupla = new DuplaAttVal();
				dupla.attId = columnas.get(i);
				dupla.valor = current.get(i);
				linea.add(dupla);
			}
		}
		
		return linea;
	}
	
}
