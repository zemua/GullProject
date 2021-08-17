package devs.mrp.gullproject.service.linea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.controller.linea.LineaController;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaCliente;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domainsdto.BooleanWrapper;
import devs.mrp.gullproject.domainsdto.StringListWrapper;
import devs.mrp.gullproject.domainsdto.linea.AtributoForLineaFormDto;
import devs.mrp.gullproject.domainsdto.linea.CosteLineaProveedorDto;
import devs.mrp.gullproject.domainsdto.linea.LineaWithAttListDto;
import devs.mrp.gullproject.domainsdto.linea.LineaWithSelectorDto;
import devs.mrp.gullproject.domainsdto.linea.MultipleLineaWithAttListDto;
import devs.mrp.gullproject.domainsdto.linea.QtyRemapper;
import devs.mrp.gullproject.domainsdto.linea.QtyRemapperWrapper;
import devs.mrp.gullproject.domainsdto.linea.StringListOfListsWrapper;
import devs.mrp.gullproject.domainsdto.linea.WrapLineasDto;
import devs.mrp.gullproject.domainsdto.linea.WrapLineasWithSelectorDto;
import devs.mrp.gullproject.domainsdto.linea.proveedor.CostRemapper;
import devs.mrp.gullproject.domainsdto.linea.proveedor.CostRemappersWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.AtributoForFormDto;
import devs.mrp.gullproject.domainsdto.propuesta.AttRemaper;
import devs.mrp.gullproject.domainsdto.propuesta.AttRemapersWrapper;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.ClassDestringfier;
import devs.mrp.gullproject.service.CompoundedConsultaLineaService;
import devs.mrp.gullproject.service.ConsultaService;
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
	LineaService lineaService;
	CompoundedConsultaLineaService compoundedService;
	@Autowired LineaFactory lineaFactory;
	
	String tipoCoste = "COSTE";
	String tipoQty = "QTY";
	
	@Autowired
	public LineaUtilities(ConsultaService consultaService, ModelMapper modelMapper, AtributoServiceProxyWebClient atributoService, LineaService lineaService, CompoundedConsultaLineaService compoundedService) {
		this.consultaService = consultaService;
		this.modelMapper = modelMapper;
		this.atributoService = atributoService;
		this.lineaService = lineaService;
		this.compoundedService = compoundedService;
	}
	
	private boolean isValidQuantity(String s) {
		return s.matches("^[1-9]\\d*$");
	}
	
	private Mono<LineaWithAttListDto> addCostsIfApplies(LineaWithAttListDto dto) {
		return consultaService.findPropuestaByPropuestaId(dto.getLinea().getPropuestaId())
			.map(rProp -> {
				if (rProp instanceof PropuestaProveedor) {
					log.debug("going to set costs on dto of: " + rProp.toString());
					dto.setCostesProveedor(((PropuestaProveedor)rProp).getCostes().stream().map(cost -> modelMapper.map(cost, CosteLineaProveedorDto.class)).collect(Collectors.toList()));
					log.debug("costs set on dto as: ");
					dto.getCostesProveedor().stream().forEach(c -> log.debug(c.toString()));
					var lineaOp = dto.getLinea().operations();
					log.debug("going to retrieve costes from: " + dto.getLinea().toString());
					dto.getCostesProveedor().forEach(cozte -> {
						log.debug("going to set for: " + cozte + " the value: " + lineaOp.getCosteByCosteId(cozte.getId()).getValue());
						cozte.setValue(lineaOp.getCosteByCosteId(cozte.getId()).getValue());
					});
				}
				return dto;
			});
	}

	public Mono<LineaWithAttListDto> getAttributesOfProposal(Linea lLinea, String propuestaId, Integer qtyLineas) {
		log.debug("going to create dto from: " + lLinea.toString());
		return consultaService.findAttributesByPropuestaId(propuestaId)
				.map(rAttProp -> modelMapper.map(rAttProp, AtributoForLineaFormDto.class)).map(rAttForForm -> {
					LineaOperations operations = new LineaOperations(lLinea);
					rAttForForm.setValue(operations.getValueByAttId(rAttForForm.getId()));
					return rAttForForm;
				}).collectList().flatMap(rAttFormList -> Mono
						.just(new LineaWithAttListDto(lLinea, new ValidList<AtributoForLineaFormDto>(rAttFormList), qtyLineas)))
						.flatMap(dto -> {
							log.debug("going to add costs for: " + lLinea.toString());
							return addCostsIfApplies(dto);
						});
	}

	public Mono<LineaWithAttListDto> getAttributesOfProposal(Mono<Linea> lLinea, Integer qtyLineas) {
		return lLinea.flatMap(linea -> getAttributesOfProposal(linea, linea.getPropuestaId(), qtyLineas));
	}
	
	public Flux<LineaWithAttListDto> getAttributesOfProposal(Flux<Linea> lineas, String propuestaId) {
		return lineas.flatMap(rLinea -> {
			return getAttributesOfProposal(rLinea, propuestaId, 1);
		})
				.map(rDto -> {
					if (rDto.getLinea().getOrder() == null) {
						rDto.getLinea().setOrder(0);
					}
					return rDto;
				})
				.collectSortedList((l1, l2) -> l1.getLinea().getOrder().compareTo(l2.getLinea().getOrder()))
				.flatMapMany(rList -> Flux.fromIterable(rList));
	}
	
	public Mono<MultipleLineaWithAttListDto> getWrappedLineasWithAttListDtoFromPropuestaId(String propuestaId) {
		Flux<Linea> lineas = lineaService.findByPropuestaId(propuestaId);
		return getAttributesOfProposal(lineas, propuestaId)
				.collectList().map(listOfDtos -> {
					MultipleLineaWithAttListDto multiple = new MultipleLineaWithAttListDto();
					multiple.setLineaWithAttListDtos(listOfDtos);
					return multiple;
				});
	}
	
	public Mono<WrapLineasWithSelectorDto> getWrappedLinesWithSelectorFromPropuestaId(String propuestaId) {
		return lineaService.findByPropuestaId(propuestaId).map(rl -> {
			LineaWithSelectorDto dto = modelMapper.map(rl, LineaWithSelectorDto.class);
			dto.setSelected(false);
			return dto;
		}).collectList().flatMap(rList -> {
			WrapLineasWithSelectorDto wrap = new WrapLineasWithSelectorDto();
			wrap.setLineas(rList);
			return Mono.just(wrap);
		});
	}
	
	public List<LineaWithSelectorDto> removeNotSelectedFromWrap(WrapLineasWithSelectorDto wrapLineasWithSelectorDto) {
		List<LineaWithSelectorDto> lineas = wrapLineasWithSelectorDto.getLineas();
		Iterator<LineaWithSelectorDto> iterator = lineas.iterator();
		while (iterator.hasNext()) {
			LineaWithSelectorDto dto = iterator.next();
			if (!dto.getSelected()) {
				iterator.remove();
			}
		}
		return lineas;
	}
	
	public Mono<Void> deleteSelectedLinesFromWrap(WrapLineasWithSelectorDto wrapLineasWithSelectorDto, String idPropuesta) {
		removeNotSelectedFromWrap(wrapLineasWithSelectorDto);
		var idLineas = wrapLineasWithSelectorDto.getLineas().stream().map(l -> l.getId()).collect(Collectors.toList());
		return consultaService.removeVariasLineasDePropuesta(idPropuesta, idLineas)
				.flatMap(delCount -> {
					return lineaService
					.deleteVariasLineas(Flux.fromIterable(wrapLineasWithSelectorDto.getLineas()).map(rLineaDto -> {
						Linea linea = modelMapper.map(rLineaDto, Linea.class);
						return linea;
					}));
				});
	}
	
	public Flux<LineaWithAttListDto> assertBindingResultOfWrappedMultipleLines(MultipleLineaWithAttListDto multipleLineaWithAttListDto, BindingResult bindingResult) {
		return Flux.fromIterable(multipleLineaWithAttListDto.getLineaWithAttListDtos()).index().flatMap(rTuple -> {
			return assertBindingResultOfListDto(rTuple.getT2(), bindingResult, "lineaWithAttListDtos[" + rTuple.getT1() + "].attributes")
					.then(Mono.just(assertNameBindingResultOfListDto(rTuple.getT2(), bindingResult, "lineaWithAttListDtos[" + rTuple.getT1() + "].linea.nombre")))
					.then(Mono.just(rTuple.getT2()));
		});
	}
	
	public Flux<LineaWithAttListDto> updateLinesFromListOfLinesWithAttListDto(List<LineaWithAttListDto> lineasDto, String propuestaId) {
		log.debug("received lineasDto to update in db " + lineasDto.toString());
		return getAttributesOfProposal(lineaService.updateVariasLineas(Flux.fromIterable(lineasDto)
				.flatMap(oDto -> reconstructLine(oDto))), propuestaId);
	}
	
	public MultipleLineaWithAttListDto wrapLinesIntoMultipleObject(List<LineaWithAttListDto> lineasDto) {
		MultipleLineaWithAttListDto multiple = new MultipleLineaWithAttListDto();
		multiple.setLineaWithAttListDtos(lineasDto);
		multiple.getLineaWithAttListDtos().forEach(sLineAtt -> {
			if (sLineAtt.getLinea().getOrder() == null) {
				sLineAtt.getLinea().setOrder(0);
			}
		});
		multiple.getLineaWithAttListDtos().sort((a, b) -> a.getLinea().getOrder().compareTo(b.getLinea().getOrder()));
		return multiple;
	}
	
	public Flux<Linea> addSeveralCopiesOfSameLineDto (LineaWithAttListDto lineaWithAttListDto, String propuestaId) {
		log.debug("llamada a addSeveralCopiesOfSameLineDto");
		Flux<Linea> l1;
		Mono<List<Linea>> llineas = reconstructLine(lineaWithAttListDto)
				.map(rLine -> {
					log.debug("mapping reconstructed line");
					List<Linea> lista = new ArrayList<>();
					LineaOperations operationsRline = new LineaOperations(rLine);
					for (int i=0; i<lineaWithAttListDto.getQty(); i++) {
						Linea dLine = lineaFactory.from(rLine);
						dLine.setQty(1); // create several lines, each with qty 1
						log.debug("añadimos linea: " + dLine.toString());
						lista.add(dLine);
					}
					log.debug("en total hay lineas: " + lista.toString());
					return lista;
				});
		l1 = lineaService.addVariasLineas(llineas.flatMapMany(ll -> Flux.fromIterable(ll)), propuestaId);
		return l1;
	}

	public Flux<Boolean> assertBindingResultOfListDto(LineaWithAttListDto lineaWithAttListDto, BindingResult bindingResult, String attsRoute) {
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
								log.debug("going to reject: " + attsRoute + "[" + pos + "].id");
								bindingResult.rejectValue(attsRoute + "[" + pos + "].id",
										"error." + attsRoute + "[" + pos + "]",
										"El valor no es correcto para este atributo.");
							}
							return rBool;
						});
			} else {
				return Mono.just(true);
			}
		});
	}
	
	public boolean assertNameBindingResultOfListDto(LineaWithAttListDto lineaWithAttListDto, BindingResult bindingResult, String nameRoute) {
		String nombre = lineaWithAttListDto.getLinea().getNombre();
		boolean isValid = true;
		if (nombre == null || nombre.equals("")) {
			isValid = false;
			log.debug("going to reject an empty name: " + nameRoute);
			bindingResult.rejectValue(nameRoute,
					"error." + nameRoute,
					"El nombre de esta línea no es válido");
		}
		return isValid;
	}

	public Mono<Linea> reconstructLine(LineaWithAttListDto lineaWithAttListDto) {
		Linea nLinea = lineaWithAttListDto.getLinea();
		List<CosteLineaProveedorDto> costesDto = lineaWithAttListDto.getCostesProveedor();
		if (costesDto != null) {
			if (nLinea.getCostesProveedor() == null) {
				nLinea.setCostesProveedor(new ArrayList<>());
			}
			costesDto.stream().forEach(dto -> {
				CosteLineaProveedor coste = new CosteLineaProveedor();
				coste.setCosteProveedorId(dto.getId());
				coste.setValue(dto.getValue());
				nLinea.getCostesProveedor().add(coste);
			});
		}
		List<AtributoForLineaFormDto> nAtts = lineaWithAttListDto.getAttributes();
		if (nAtts == null) {
			nAtts = new ValidList<>();
		}
		return Flux.fromIterable(nAtts)
				.flatMap(rAtt -> atributoService.getClassTypeOfFormat(rAtt.getTipo()).map(
						rClass -> new Campo<Object>(rAtt.getId(), ClassDestringfier.toObject(rClass, rAtt.getValue()))))
				.collectList().map(rCampoList -> {
					LineaOperations operations = new LineaOperations(nLinea);
					operations.replaceOrAddCamposObj(rCampoList);
					return nLinea;
				});
	}
	
	public StringListOfListsWrapper excelTextToLineObject(String texto) {
		// arrange the lines in an object that can be used in Thymeleaf Template
		String lines[] = texto.split("\\R");
		Integer nOfCols = 0;
		StringListOfListsWrapper fieldArrays = new StringListOfListsWrapper();
		for (int i = 0; i<lines.length; i++) {
			List<String> fl = new ArrayList<>(Arrays.asList(lines[i].split("\\t")));
			if (fl.size() > nOfCols) {
				nOfCols = fl.size();
			}
			while (fl.size() < nOfCols) {
				fl.add("");
			}
			fieldArrays.add(new StringListWrapper(fl, "")); // we didn't retrieve the name yet so we use an empty string in the meanwhile
		}
		
		// add missing fields on the first lines if jumped over
		for (int i = 0; i<lines.length; i++) {
			var fields = fieldArrays.getStringListWrapper().get(i).getString();
			if (fields.size() >= nOfCols) {
				break;
			}
			while (fields.size() < nOfCols) {
				fields.add("");
			}
		}
		
		for (int i = 0; i<nOfCols; i++) {
			// it is a bit dumb but is the best way to match the fields in thymeleaf and get the selected value back
			fieldArrays.add("");
			fieldArrays.addName(null);
		}
		
		return fieldArrays;
	}
	
	public Mono<WrapLineasDto> wrapLineasDtoFromPropuestaId(String propuestaId) {
		return lineaService.findByPropuestaId(propuestaId)
				.collectList().flatMap(rList -> {
					WrapLineasDto wrap = new WrapLineasDto();
					wrap.setLineas(rList);
					return Mono.just(wrap);
				});
	}
	
	public Mono<StringListOfListsWrapper> stringListOfListsFromPropuestaId(String propuestaId) {
		return lineaService.findByPropuestaId(propuestaId)
				.collectList().flatMap(rList -> {
					return consultaService.findPropuestaByPropuestaId(propuestaId)
						.flatMap(rProp -> {
							return Mono.just(stringListOfListsFromPropuestaAndLineas(rProp, rList));
						});
				})
				;
	}
	
	private StringListOfListsWrapper stringListOfListsFromPropuestaAndLineas(Propuesta propuesta, List<Linea> lineas) {
		StringListOfListsWrapper wrap = new StringListOfListsWrapper();
		wrap.setStrings(propuesta.getAttributeColumns().stream().map(att -> att.getName()).collect(Collectors.toList()));
		log.debug("tenemos una lista de " + wrap.getStrings().size() + " strings tal que " + wrap.getStrings().toString());
		propuesta.getAttributeColumns().stream().forEach(att -> {
			log.debug("we add name field for one att " + att.toString());
			wrap.getName().add(null);
		});
		if (propuesta instanceof PropuestaProveedor) {
			((PropuestaProveedor)propuesta).getCostes().stream().forEach(cos -> {
				log.debug("we add name field for one cost " + cos.toString());
				wrap.getName().add(null);
				wrap.getStrings().add(cos.getName());
			});
		}
		lineas.stream().forEach(sLine -> {
			LineaOperations sLineOp = sLine.operations();
			StringListWrapper stringListWrapper = new StringListWrapper();
			stringListWrapper.setString(new ArrayList<>());
			stringListWrapper.setName(sLine.getNombre());
			stringListWrapper.setId(sLine.getId());
			propuesta.getAttributeColumns().stream().forEach(att -> {
				stringListWrapper.add(sLineOp.getCampoByAttId(att.getId()).getDatosText());
			});
			if (propuesta instanceof PropuestaProveedor) {
				((PropuestaProveedor)propuesta).getCostes().stream().forEach(cos -> stringListWrapper.add(String.valueOf(sLineOp.getCosteByCosteId(cos.getId()).getValue())));
			}
			wrap.getStringListWrapper().add(stringListWrapper);
		});
		return wrap;
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
		log.debug("vamos a mirar errores en los campos");
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
		
		return consultaService.findPropuestaByPropuestaId(propuestaId)
		.flatMapMany(rPro1 -> {
			return mapOfAttIdsToTipo(propuestaId)
				.flatMapMany(rAttToTipo -> {
					return Flux.fromIterable(tuplas)
							.flatMap(fTupla -> {
								fTupla.tipo = rAttToTipo.get(fTupla.attId);
								if (fTupla.attId != null && !fTupla.attId.equals("")) {
										//return consultaService.findPropuestaByPropuestaId(propuestaId) // moved to start of function tree
										return Mono.just(rPro1)
											.flatMap(rPro -> {
												var operations = rPro.operations();
												if (operations.ifIsAttributeId(fTupla.attId)) { // if it is an attribute validate it
												return atributoService.validateDataFormat(fTupla.tipo, fTupla.valor)
														.map(rBool -> {
															log.debug("respuesta de atributo service para tipo " + fTupla.tipo + " y valor " + fTupla.valor + " es " + rBool);
															fTupla.validado = rBool;
															return fTupla;
														});
												} else if (rPro instanceof PropuestaProveedor && ((PropuestaProveedor)rPro).operationsProveedor().ifIsCosteProveedorId(fTupla.attId)) { // if it is a cost validate double value
													log.debug("es un costeProveedor, vamos a validarlo: " + ((PropuestaProveedor)rPro).operationsProveedor().ifValidCosteValue(fTupla.valor));
													log.debug("de la propuesta: " + rPro.toString());
													fTupla.validado = ((PropuestaProveedor)rPro).operationsProveedor().ifValidCosteValue(fTupla.valor);
													return Mono.just(fTupla);
												} else if (fTupla.attId.equals(LineaController.qtyvalue)) {
													// it is a quantity
													fTupla.validado = isValidQuantity(fTupla.valor);
													return Mono.just(fTupla);
												}
												else { // if neither of these, then we don't know what it is, reject
													log.debug("este campo no encontramos a qué se refiere");
													if (rPro instanceof PropuestaProveedor) {
														log.debug("la propuesta es instancia de PropuestaProveedor");
														log.debug("la id de la tupla es " + fTupla.attId);
														log.debug("los costes de la propuesta son: " + ((PropuestaProveedor)rPro).getCostes().toString());
													}
													fTupla.validado = false;
													return Mono.just(fTupla);
												}
											});
										
								} else { // it is a field that we are not going to use, so any value is ok
									fTupla.validado = true;
									return Mono.just(fTupla);
								}
							});
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
						Linea linea = lineaFactory.create();
						linea.setCostesProveedor(new ArrayList<>());
						sDupla.stream().forEach(sField -> {
							if (sField.clase.equals(tipoCoste)){
								CosteLineaProveedor coste = new CosteLineaProveedor();
								coste.setCosteProveedorId(sField.attId);
								coste.setValue(Double.parseDouble(sField.valor.replace(",", ".")));
								linea.getCostesProveedor().add(coste);
							} else if (sField.clase.equals(tipoQty)) {
								linea.setQty(Integer.parseInt(sField.valor));
							}
							else {
								Campo<Object> campo = new Campo<>();
								campo.setAtributoId(sField.attId);
								log.debug("vamos a llamar a classDestringfier con clase " + sField.clase + " y valor " + sField.valor);
								campo.setDatos(ClassDestringfier.toObject(sField.clase, sField.valor));
								log.debug("hemos obtenido los datos " + campo.getDatosText());
								LineaOperations operations = new LineaOperations(linea);
								operations.addCampo(campo);
							}
						});
						linea.setPropuestaId(propuestaId);
						linea.setNombre(names.get(sDupla.get(0).linea));
						if (linea.getQty() == null) {
							linea.setQty(1);
						}
						listOfLineas.add(linea);
					});
					return listOfLineas;
				});
	}
	
	private Mono<List<List<DuplaAttVal>>> allLineasInDuplaCompleta(StringListOfListsWrapper wrapper, String propuestaId) throws Exception {
		List<List<DuplaAttVal>> duplas = allLineasInDuplaWithAttidAndValor(wrapper);
		return mapOfAttIdsToTipo(propuestaId)
			.flatMap(rAttIdToTipo -> {
				return consultaService.findPropuestaByPropuestaId(propuestaId)
					.flatMap(rProp -> {
						var operations = rProp.operations();
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
										if (operations.ifHasAttributeColumn(rCampo.attId)) {
											log.debug("es un atributo");
											log.debug("vamor a recoger del tipo de " + rCampo.attId + " desde el mapa " + rAttIdToTipo.toString());
											rCampo.tipo = rAttIdToTipo.get(rCampo.attId);
											log.debug("vamos a recoger la clase de " + rCampo.attId + " desde el mapa " + attIdToClass.toString());
											rCampo.clase = attIdToClass.get(rCampo.attId);
											log.debug("añadimos tipo " + rCampo.tipo + " y clase " + rCampo.clase + " a este campo: ");
										} else if (rProp instanceof PropuestaProveedor && ((PropuestaProveedor)rProp).operationsProveedor().ifIsCosteProveedorId(rCampo.attId)) {
											log.debug("es un coste, ponemos tipo y clase a COSTE");
											rCampo.tipo = tipoCoste;
											rCampo.clase = tipoCoste;
										} else if(rCampo.attId.equals(LineaController.qtyvalue)) {
											log.debug("es cantidad, ponemos tipo y clase a QTY");
											rCampo.tipo = tipoQty;
											rCampo.clase = tipoQty;
										}
									});
								});
								log.debug("devolvemos estas duplas en allLineasInDuplaCompleta: " + rDuplas.toString());
								return rDuplas;
							}));
					});
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
	
	
	/****
	 * 
	 * Various functions
	 * 
	 */
	
	public Map<String, Integer> linesWrapToMapOf_Id_vs_Order(WrapLineasDto wrapLineasDto) {
		Map<String, Integer> map = new HashMap<>();
		wrapLineasDto.getLineas().stream().forEach(sLinea -> {
			map.put(sLinea.getId(), sLinea.getOrder());
		});
		return map;
	}
	
	public Flux<Linea> updateNombresFromStringListOfListsWrapper(StringListOfListsWrapper stringListOfListsWrapper) {
		return Flux.fromIterable(stringListOfListsWrapper.getStringListWrapper())
				.flatMap(rWrap -> {
					log.debug("vamos a llamar updateNombre con " + rWrap.toString());
					return lineaService.updateNombre(rWrap.getId(), rWrap.getName());
				});
	}
	
	public Mono<AttRemapersWrapper> getRemappersFromPropuestaAndAttId(String propuestaId, String localIdentifier) {
		return consultaService.findPropuestaByPropuestaId(propuestaId)
				.flatMap(rProp -> {
					log.debug("en la propuesta " + rProp.toString());
					Optional<AtributoForCampo> att = rProp.getAttributeColumns().stream().filter(at -> at.getLocalIdentifier().equals(localIdentifier)).findFirst();
					AtributoForCampo attb = new AtributoForCampo();
					attb.setTipo("DESCRIPCION");
					attb.setName("DESCRIPCION");
					return lineaService.findByPropuestaId(propuestaId)
						.map(rLine -> {
							log.debug("para la linea" + rLine.toString());
							AttRemaper maper = new AttRemaper();
							Campo<?> campo = rLine.operations().getCampoByAttId(att.orElse(attb).getId());
							if (campo != null) {
								maper.setBefore(campo.getDatosText());
								maper.setAfter(campo.getDatosText());
							} else {
								maper.setBefore("");
								maper.setAfter("");
							}
							maper.setAtributoId(att.orElse(attb).getId());
							maper.setLocalIdentifier(localIdentifier);
							maper.setName(att.orElse(attb).getName());
							maper.setTipo(att.orElse(attb).getTipo());
							log.debug("devolviendo " + maper.toString());
							return maper;
						})
						.distinct(AttRemaper::getBefore).collectList()
						.map(rList -> {
							log.debug("después de filtrado: " + rList.toString());
							return new AttRemapersWrapper(rList);
						});
				});
	}
	
	public Mono<CostRemappersWrapper> getRemappersFromPropuestaAndCost(String propuestaId, String costeId){
		return consultaService.findPropuestaByPropuestaId(propuestaId)
				.flatMap(rProp -> {
					if (!(rProp instanceof PropuestaProveedor)) {
						return Mono.empty();
					}
					PropuestaProveedor propuesta = (PropuestaProveedor)rProp;
					Optional<CosteProveedor> cost = propuesta.getCostes().stream().filter(co -> co.getId().equals(costeId)).findFirst();
					return lineaService.findByPropuestaId(propuestaId)
						.map(rLine -> {
							var lin = rLine;
							CostRemapper maper = new CostRemapper();
							CosteLineaProveedor coste = lin.operations().getCosteByCosteId(costeId);
							if (coste != null) {
								maper.setBefore(coste.getValue());
								maper.setAfter(coste.getValue());
							} else {
								maper.setBefore(0D);
								maper.setAfter(0D);
							}
							maper.setCosteProveedorId(costeId);
							log.debug("devolviendo cost maper: " + maper.toString());
							return maper;
						})
						.distinct(CostRemapper::getBefore).collectList()
						.map(rList -> {
							log.debug("después de filtrado: " + rList.toString());
							return new CostRemappersWrapper(rList);
						})
						;
				})
				;
	}
	
	public Mono<QtyRemapperWrapper> getQtyRemappersFromPropuesta(String propuestaId) {
		return lineaService.findByPropuestaId(propuestaId)
				.map(rLine -> {
					QtyRemapper maper = new QtyRemapper();
					int qty;
					if (rLine.getQty() != null) {
						qty = rLine.getQty();
					} else {
						qty = 1;
					}
					maper.setBefore(qty);
					maper.setAfter(qty);
					return maper;
				})
				.distinct(QtyRemapper::getBefore).collectList()
				.map(rList -> {
					return new QtyRemapperWrapper(rList);
				});
	}
	
	public Mono<Map<String, Set<String>>> get_ProposalId_VS_SetOfCounterLineId(String customerProposalId) {
		return compoundedService.getAllLineasOfPropuestasAssignedTo(customerProposalId)
			.collectList().map(lineas -> {
				Map<String, Set<String>> map = new HashMap<>();
				lineas.stream().forEach(linea -> {
					if (!map.containsKey(linea.getPropuestaId())) {
						map.put(linea.getPropuestaId(), ConcurrentHashMap.newKeySet());
					}
					if (linea.getCounterLineId() != null) {
						linea.getCounterLineId().stream().forEach(counter -> {
							map.get(linea.getPropuestaId()).add(counter);
						});
					}
				});
				return map;
			});
	}
	
}
