package devs.mrp.gullproject.controller.linea;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.controller.pojos.SelectableLinesWrapPojo;
import devs.mrp.gullproject.controller.pojos.SelectableLinesWrapPojoConverter;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLineFactory;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLinesWrap;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLinesWrapBuilder;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.AttRemaperUtilities;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.facade.OfferAndLinesServiceFacade;
import devs.mrp.gullproject.service.facade.SupplierLineFinderByProposalAssignation;
import devs.mrp.gullproject.service.linea.CustomerLineToCostMapper;
import devs.mrp.gullproject.service.linea.LineByAssignationRetriever;
import devs.mrp.gullproject.service.linea.LineaOfferService;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.linea.LineaUtilities;
import devs.mrp.gullproject.service.linea.oferta.pvpmapper.PvpMapperByAssignedLineAbstractFactory;
import devs.mrp.gullproject.service.linea.oferta.pvpmapper.SumMapperByAssignedLineAbstractFactory;
import devs.mrp.gullproject.service.linea.oferta.selectable.LineReConstructor;
import devs.mrp.gullproject.service.linea.proveedor.CostMapperByIdFactory;
import devs.mrp.gullproject.service.linea.proveedor.CostRemapperUtilities;
import devs.mrp.gullproject.service.linea.proveedor.LineAttributeConcatenator;
import devs.mrp.gullproject.service.linea.proveedor.LineAttributeConcatenatorFactory;
import devs.mrp.gullproject.service.linea.proveedor.SupplierLineMapperByPropAndAssignedLineFactory;
import devs.mrp.gullproject.service.linea.proveedor.TotalCostOfAllLinesFinderFactory;
import devs.mrp.gullproject.service.propuesta.AttributesExtractor;
import devs.mrp.gullproject.service.propuesta.oferta.FromPropuestaToOfertaFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.FromPropuestaToProveedorFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.ProposalCostNameMapperFromPvpFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.PropuestaProveedorUtilities;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping(path = "/lineas")
public class AssignLinesInOfferController extends LineaControllerSetup {

	@Autowired SupplierLineMapperByPropAndAssignedLineFactory supplierLineMapperByPropProvIdAndCounterLineId;
	@Autowired FromPropuestaToOfertaFactory ofertaConverter;
	@Autowired ProposalCostNameMapperFromPvpFactory costFromPvpMapper;
	@Autowired FromPropuestaToProveedorFactory proveedorFactory;
	@Autowired CostMapperByIdFactory lineCostByCostIdMapper;
	@Autowired SelectableLinesWrapBuilder selectableLinesWrapBuilder;
	@Autowired LineaOfferService lineaOfertaService;
	@Autowired PvpMapperByAssignedLineAbstractFactory pvperMapper;
	@Autowired SumMapperByAssignedLineAbstractFactory pvperSumMapper;
	@Autowired SelectableLineFactory selectableFactory;
	@Autowired TotalCostOfAllLinesFinderFactory totalCostMapperFactory;
	@Autowired LineAttributeConcatenatorFactory concatenatorFactory;
	
	@Autowired SelectableLinesWrapPojoConverter wrapSelectablePojoConverter;
	@Autowired AttributesExtractor attributesExtractor;
	@Autowired LineReConstructor lineReconstructor;
	@Autowired OfferAndLinesServiceFacade offerLinesFacade;
	
	public AssignLinesInOfferController(LineaService lineaService, ConsultaService consultaService,
			AtributoServiceProxyWebClient atributoService, LineaUtilities lineaUtilities,
			AttRemaperUtilities attRemaperUtilities, CostRemapperUtilities costRemapperUtilities,
			PropuestaProveedorUtilities propuestaProveedorUtilities,
			SupplierLineFinderByProposalAssignation finder) {
		super(lineaService, consultaService, atributoService, lineaUtilities, attRemaperUtilities, costRemapperUtilities,
				propuestaProveedorUtilities, finder);
	}
	
	@ModelAttribute("selectableLinesWrap")
	public SelectableLinesWrap getSelectableLinesWrap(SelectableLinesWrapPojo pojo) {
		if (pojo.getLineas() == null) {return null;}
		return wrapSelectablePojoConverter.fromPojo(pojo);
	}
	
	@GetMapping("/allof/ofertaid/{propuestaId}")
	public Mono<String> showAllLinesOfOferta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		return consultaService.findConsultaByPropuestaId(propuestaId)
		.flatMap(rCons -> {
			log.debug("consulta: " + rCons.toString());
			var ops = rCons.operations();
			PropuestaNuestra propuesta = (PropuestaNuestra)ops.getPropuestaById(propuestaId);
			log.debug("propuestaNuestra: " + propuesta.toString());
			Propuesta propCliente = ops.getPropuestaById(propuesta.getForProposalId());
			log.debug("propuestaCliente: " + propCliente.toString());
			
			model.addAttribute("propuesta", propuesta);
			model.addAttribute("consulta", rCons);
			model.addAttribute("propCliente", propCliente);
			model.addAttribute("lineasCliente", new ReactiveDataDriverContextVariable(lineaService.findByPropuestaId(propCliente.getId()), 1));
			
			return supplierLineFinderByProposalAssignation.findBy(propCliente.getId())
				.collectList()
				.map(proveedorLineList -> {
					MyListOfAsignables<Linea> asignablesProveedor = new LineByAssignationRetriever<>(proveedorLineList);
					model.addAttribute("costMapper", new CustomerLineToCostMapper(asignablesProveedor));
					return true;
				})
				.then(lineaOfertaService.findByPropuesta(propuesta.getId()).collectList())
				.map(ofertaLineList -> {
					var pvpMapper = pvperMapper.from(ofertaLineList);
					var sumMapper = pvperSumMapper.from(propuesta, ofertaLineList);
					model.addAttribute("pvpMapper", pvpMapper);
					model.addAttribute("sumMapper", sumMapper);
					return true;
				})
				;
		})
		.then(Mono.just("showAllLineasOfOferta"));
	}

	@GetMapping("/allof/ofertaid/{propuestaId}/assign")
	public Mono<String> assignLinesOfOferta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		return consultaService.findConsultaByPropuestaId(propuestaId)
				.flatMap(rConsulta -> {
					model.addAttribute("consulta", rConsulta);
					var consultaOps = rConsulta.operations();
					var propuestaNuestra = ofertaConverter.from(consultaOps.getPropuestaById(propuestaId));
					var propuestaCliente = consultaOps.getPropuestaById(propuestaNuestra.getForProposalId());
					// Supplier lines mapper
					return supplierLineFinderByProposalAssignation.findBy(propuestaNuestra.getForProposalId())
						.collectList()
						.flatMap(proveedorLines -> {
							var costMapper = costFromPvpMapper.from(propuestaNuestra, consultaOps.getPropuestasProveedorAssignedTo(propuestaNuestra.getForProposalId()).stream().map(p -> proveedorFactory.from(p)).collect(Collectors.toList()));
							var lineCostMapper = lineCostByCostIdMapper.from(proveedorLines);
							model.addAttribute("supplierLineMapper", supplierLineMapperByPropProvIdAndCounterLineId.from(proveedorLines));
							model.addAttribute("proposalCostMapperToPVp", costMapper);
							model.addAttribute("lineCostByCostIdMapper", lineCostMapper);
							return lineaService.findByPropuestaId(propuestaNuestra.getForProposalId())
							.collectList()
							.map(customerLinesList -> {
								model.addAttribute("totalCostMapper", totalCostMapperFactory.from(costMapper, lineCostMapper, customerLinesList.stream().map(l -> l.getId()).collect(Collectors.toList())));
								return true;
							});
						})
					// Offer pvps, sums, margins mappers
						.thenMany(lineaOfertaService.findByPropuesta(propuestaId))
						.collectList()
						.flatMap(offerLines -> {
							model.addAttribute("propuestaNuestra", propuestaNuestra);
							model.addAttribute("pvps", propuestaNuestra.getPvps());
							model.addAttribute("pvpMapper", pvperMapper.from(offerLines));
							return lineaService.findByPropuestaId(propuestaNuestra.getForProposalId())
								.collectList()
								.map(customerLinesList -> {
					// Customer lines
									model.addAttribute("propuestaCliente", propuestaCliente);
									model.addAttribute("lineasCliente", customerLinesList);
									model.addAttribute("selectableLinesWrap", selectableLinesWrapBuilder.from(customerLinesList, selectableFactory.from(offerLines), propuestaNuestra));
									return true;
								});
						})
						;
				})
		.then(Mono.just("assignLinesOfOferta"));
	}
	
	@PostMapping("/allof/ofertaid/{propuestaId}/assign") // TODO test
	public Mono<String> processAssignLinesOfOferta(@ModelAttribute("selectableLinesWrap") SelectableLinesWrap selectableLinesWrap, BindingResult bindingResult, Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		return consultaService.findConsultaByPropuestaId(propuestaId)
				.flatMap(rConsulta -> {
					model.addAttribute("consulta", rConsulta);
					var consultaOps = rConsulta.operations();
					var propuestaNuestra = ofertaConverter.from(consultaOps.getPropuestaById(propuestaId));
					var propuestaCliente = consultaOps.getPropuestaById(propuestaNuestra.getForProposalId());
			// SUPPLIER LINES
					return supplierLineFinderByProposalAssignation.findBy(propuestaNuestra.getForProposalId())
						.collectList()
						.flatMap(proveedorLines -> {
						LineAttributeConcatenator concatenator = concatenatorFactory.from(proveedorLines);
				// OFFER LINES
						var atributos = attributesExtractor.fromProposals(consultaOps.getPropuestasProveedorAssignedTo(propuestaNuestra.getForProposalId()));
						var lineasOferta = lineReconstructor.from(selectableLinesWrap, concatenator, atributos, propuestaNuestra.getId());
						return offerLinesFacade.clearAllLinesOfOffer(propuestaId)
								.thenMany(offerLinesFacade.saveAll(propuestaNuestra.getId(), lineasOferta))
								.collectList()
								.flatMap(offerLines -> {
									model.addAttribute("lineas", offerLines);
									model.addAttribute("atributos", atributos);
									model.addAttribute("propuestaNuestra", propuestaNuestra);
									model.addAttribute("pvps", propuestaNuestra.getPvps());
									model.addAttribute("pvpMapper", pvperMapper.from(offerLines));
							// CUSTOMER LINES
									return lineaService.findByPropuestaId(propuestaNuestra.getForProposalId())
										.collectList()
										.map(customerLinesList -> {
											model.addAttribute("propuestaCliente", propuestaCliente);
											model.addAttribute("lineasCliente", customerLinesList);
											model.addAttribute("selectableLinesWrap", selectableLinesWrapBuilder.from(customerLinesList, selectableFactory.from(offerLines), propuestaNuestra));
											return true;
										});
								});
						});
				})
		.then(Mono.just("processAssignLinesOfOferta"));
	}
	
}
