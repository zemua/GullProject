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

	public Mono<LineaWithAttListDto> getAttributesOfProposal(Linea lLinea, String propuestaId, Integer qtyLineas) {
		return consultaService.findAttributesByPropuestaId(propuestaId)
				.map(rAttProp -> modelMapper.map(rAttProp, AtributoForLineaFormDto.class)).map(rAttForForm -> {
					rAttForForm.setValue(lLinea.getValueByAttId(rAttForForm.getId()));
					return rAttForForm;
				}).collectList().flatMap(rAttFormList -> Mono
						.just(new LineaWithAttListDto(lLinea, new ValidList<AtributoForLineaFormDto>(rAttFormList), qtyLineas)));
	}

	public Mono<LineaWithAttListDto> getAttributesOfProposal(Mono<Linea> lLinea, Integer qtyLineas) {
		return lLinea.flatMap(linea -> getAttributesOfProposal(linea, linea.getPropuestaId(), qtyLineas));
	}
	
	public Flux<LineaWithAttListDto> getAttributesOfProposal(Flux<Linea> lineas, String propuestaId) {
		return lineas.flatMap(rLinea -> {
			return getAttributesOfProposal(rLinea, propuestaId, 1);
		})
		;
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
		String lines[] = texto.split("\\R");
		Integer nOfCols = 0;
		StringListOfListsWrapper fieldArrays = new StringListOfListsWrapper();
		for (int i = 0; i<lines.length; i++) {
			List<String> fl = Arrays.asList(lines[i].split("\\t"));
			fieldArrays.add(new StringListWrapper(fl, "")); // we didn't retrieve the name yet so we use an empty string in the meanwhile
			if (fl.size() > nOfCols) {
				nOfCols = fl.size();
			}
		}
		
		for (int i = 0; i<nOfCols; i++) {
			// it is a bit dumb but is the best way to match the fields in thymeleaf and get the selected value back
			fieldArrays.add("");
			fieldArrays.addName(null);
		}
		
		return fieldArrays;
	}
	
	/**
	 * 
	 * below...
	 * For Validating a List of Lists from Bulk data of excel-paste in the bindingResult
	 * @throws Exception 
	 * 
	 * 
	 */
	
	private class TuplaTabla {
		Integer fila;
		Integer columna;
		String attId;
		String tipo;
		String valor;
		Boolean validado;
	}
	
	public Flux<Boolean> addBulkTableErrorsToBindingResult(StringListOfListsWrapper wrapper, String propuestaId, BindingResult bindingResult) throws Exception {
		List<String> names = getNames(wrapper);
		boolean addedErrorToName = false;
		for (int i=0; i<names.size(); i++) {
			log.debug("para el nombre de la fila " + i);
			if (names.get(i) == null || names.get(i).equals("")) {
				log.debug("encontramos error y añadimos al bindingResult");
				bindingResult.rejectValue("stringListWrapper[" + i + "].name",
											"error.stringListWrapper.name",
											"Esta fila necesita un nombre");
				if (!addedErrorToName) {
					bindingResult.rejectValue("nameError", "error.stringListWrapper.nameError",
							"Estas filas necesitan un nombre");
					addedErrorToName = true;
				}
			} else {
				log.debug("no encontramos error");
			}
		}
		
		BooleanWrapper addedErrorToFields = new BooleanWrapper(false);
		return bulkTableWrapperToTuplaTabla(wrapper, propuestaId)
				.map(rTupla -> {
					if (!rTupla.validado) {
						// reject the field
						log.debug("este campo tiene error: " + rTupla.toString() + " en la fila " + rTupla.fila + " y la columna " + rTupla.columna);
						bindingResult.rejectValue("stringListWrapper[" + rTupla.fila + "].string[" + rTupla.columna + "]",
								"error.stringListOfListsWrapper.stringListWrapper[" + rTupla.fila + "].string",
								"El valor no es correcto para este atributo.");
						bindingResult.rejectValue("strings[" + rTupla.columna + "]",
								"error.stringListOfListsWrapper.strings",
								"El valor no es correcto para este atributo.");
						if (!addedErrorToFields.getB()) {
							bindingResult.rejectValue("fieldError",
									"error.stringListWrapper.fieldError",
									"Estos campos tienen un valor incorrecto");
							addedErrorToFields.setB(true);;
						}
					}
					return rTupla.validado;
				}).concatWithValues(!addedErrorToName);
	}
	
	private List<String> getNames(StringListOfListsWrapper wrapper) {
		List<String> names = new ArrayList<>();
		
		log.debug("vamos a extraer los nombres");
		for (int i=0; i<wrapper.getStringListWrapper().size(); i++) {
			log.debug("para la linea " + i);
			StringBuilder nameBuilder = new StringBuilder();
			for(int j=0; j<wrapper.getStrings().size(); j++) { // for each number (1,2,3,4...)
				for (int k=0; k<wrapper.getStrings().size(); k++) { // go column by column
					if (wrapper.getName(k) != null && wrapper.getName(k).equals(j+1)) { // If column-name(k) contains number (j+1)
						String field = wrapper.getList(i).get(k);
						if (field != null) {
							nameBuilder.append(field);
						}
					}
				}
			}
			log.debug("añadimos el nombre: " + nameBuilder.toString());
			names.add(nameBuilder.toString());
			wrapper.getList(i).setName(nameBuilder.toString());
		}
		
		return names;
	}
	
	private Flux<TuplaTabla> bulkTableWrapperToTuplaTabla(StringListOfListsWrapper wrapper, String propuestaId) throws Exception {
		List<TuplaTabla> tuplas = mapToTuplaTabla(wrapper);
		AtomicInteger counter = new AtomicInteger();
		counter.set(0);
		
		return mapOfAttIdsToTipo(propuestaId)
			.flatMapMany(rAttToTipo -> {
				return Flux.fromIterable(tuplas)
						.flatMap(fTupla -> {
							fTupla.tipo = rAttToTipo.get(fTupla.attId);
							if (fTupla.attId != null && !fTupla.attId.equals("")) {
									
										return atributoService.validateDataFormat(fTupla.tipo, fTupla.valor)
												.map(rBool -> {
													log.debug("respuesta de atributo service para tipo " + fTupla.tipo + " y valor " + fTupla.valor + " es " + rBool);
													fTupla.validado = rBool;
													return fTupla;
												});
									
							} else { // it is a field that we are not going to use
								fTupla.validado = true;
								return Mono.just(fTupla);
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
		
		log.debug("numero de columnas: " + columnas.size());
		for (int i=0; i<lineas.size(); i++) {
			log.debug("numero de campos en la linea: " + lineas.get(i).getString().size());
			for (int j=0; j<columnas.size(); j++) {
				if (columnas.size() != lineas.get(i).getString().size()) {
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
		int linea;
	}
	
	public Mono<List<Linea>> allLineasFromBulkWrapper(StringListOfListsWrapper wrapper, String propuestaId) throws Exception {
		List<String> names = getNames(wrapper);
		return allLineasInDuplaCompleta(wrapper, propuestaId)
				.map(rAllDuplas -> {
					List<Linea> listOfLineas = new ArrayList<>();
					
					rAllDuplas.stream().forEach(sDupla ->{
						log.debug("vamos a pasar estas duplas a linea: " + sDupla.toString());
						Linea linea = new Linea();
						sDupla.stream().forEach(sField -> {
							Campo<Object> campo = new Campo<>();
							campo.setAtributoId(sField.attId);
							log.debug("vamos a llamar a classDestringfier con clase " + sField.clase + " y valor " + sField.valor);
							campo.setDatos(ClassDestringfier.toObject(sField.clase, sField.valor));
							linea.addCampo(campo);
						});
						linea.setPropuestaId(propuestaId);
						linea.setNombre(names.get(sDupla.get(0).linea));
						listOfLineas.add(linea);
					});
					return listOfLineas;
				});
	}
	
	private Mono<List<List<DuplaAttVal>>> allLineasInDuplaCompleta(StringListOfListsWrapper wrapper, String propuestaId) throws Exception {
		List<List<DuplaAttVal>> duplas = allLineasInDuplaWithAttidAndValor(wrapper);
		return mapOfAttIdsToTipo(propuestaId)
			.flatMap(rAttIdToTipo -> {
				
				log.debug("tenemos este mapa de Atributo.id a Atributo.tipo " + rAttIdToTipo.toString());
				
				Map<String, String> attIdToClass = new HashMap<>();
				
				log.debug("vamos a crear un flux desde el set: " + rAttIdToTipo.keySet().toString());
				return Flux.fromIterable(rAttIdToTipo.keySet())
						// first we map the classes vs ids so we just need to make one query per att to the db
					.flatMap(rAttId -> {
						log.debug("vamos a obtener el classType del id " + rAttId + " para el tipo " + rAttIdToTipo.get(rAttId));
						return atributoService.getClassTypeOfFormat(rAttIdToTipo.get(rAttId))
								.map(rClass -> {
									log.debug("del id " +rAttId.toString() + " obtenemos el tipo " + rClass.toString() + " y lo añadimos al mapa");
									attIdToClass.put(rAttId, rClass);
									return rClass;
								});
					})
					// then we use the map to fill the remaining data
					.then(Mono.just(duplas).map(rDuplas -> {
						log.debug("tenemos este mapa de Atributo.id a Atributo.class " + attIdToClass.toString());
						rDuplas.forEach(rLinea -> {
							log.debug("en esta linea " + rLinea.toString());
							rLinea.forEach(rCampo -> {
								log.debug("vamor a recoger del tipo de " + rCampo.attId + " desde el mapa " + rAttIdToTipo.toString());
								rCampo.tipo = rAttIdToTipo.get(rCampo.attId);
								log.debug("vamos a recoger la clase de " + rCampo.attId + " desde el mapa " + attIdToClass.toString());
								rCampo.clase = attIdToClass.get(rCampo.attId);
								log.debug("añadimos tipo " + rCampo.tipo + " y clase " + rCampo.clase + " a este campo: ");
							});
						});
						log.debug("devolvemos estas duplas en allLineasInDuplaCompleta: " + rDuplas.toString());
						return rDuplas;
					}));
			});
	}
	
	private List<List<DuplaAttVal>> allLineasInDuplaWithAttidAndValor(StringListOfListsWrapper wrapper) throws Exception {
		List<List<DuplaAttVal>> lista = new ArrayList<>();
		log.debug("generando los arrays de duplas con id y valor");
		for (int i =0; i<wrapper.getStringListWrapper().size(); i++) {
			List<DuplaAttVal> aray = lineaDuplaWithAttidAndValor(wrapper, i);
			log.debug("añadimos a la lista el array " + aray.toString());
			lista.add(aray);
		}
		return lista;
	}
	
	private List<DuplaAttVal> lineaDuplaWithAttidAndValor(StringListOfListsWrapper wrapper, int lineN) throws Exception {
		log.debug("para esta linea tenemos...");
		List<String> columnas = wrapper.getStrings();
		log.debug("columnas: " + columnas.toString());
		List<DuplaAttVal> linea = new ArrayList<>();
		StringListWrapper current = wrapper.getStringListWrapper().get(lineN);
		log.debug("campos: " + current.toString());
		
		if (current.getString().size() != columnas.size()) {
			log.debug("line " + lineN + "has a length of " + current.getString().size() + " which is different from the columns of " + columnas.size());
			throw new Exception("error on line length");
		}
		
		for (int i=0; i<columnas.size(); i++) {
			if (!(columnas.get(i) == null) && !columnas.get(i).isBlank()) {
				DuplaAttVal dupla = new DuplaAttVal();
				dupla.attId = columnas.get(i);
				dupla.valor = current.get(i);
				dupla.linea = lineN;
				linea.add(dupla);
				log.debug("añadimos a la linea esta dupla" + dupla);
			} else {
				log.debug("esta columna no está asignada, no la añadimos a la línea");
			}
		}
		
		return linea;
	}
	
}
