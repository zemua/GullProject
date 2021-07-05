package devs.mrp.gullproject.controller.linea;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import devs.mrp.gullproject.ainterfaces.MyListOfAsignables;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.AttRemaperUtilities;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.facade.SupplierLineFinderByProposalAssignation;
import devs.mrp.gullproject.service.linea.CustomerLineToCostMapper;
import devs.mrp.gullproject.service.linea.LineByAssignationRetriever;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.linea.LineaUtilities;
import devs.mrp.gullproject.service.linea.oferta.PvpMapperByAssignedLineFactory;
import devs.mrp.gullproject.service.linea.oferta.PvpMapperByCounterLineFactory;
import devs.mrp.gullproject.service.linea.oferta.PvpMarginMapperByCounterIdFactory;
import devs.mrp.gullproject.service.linea.oferta.PvpSumByCounterIdFactory;
import devs.mrp.gullproject.service.linea.oferta.PvpSumForLineFinder;
import devs.mrp.gullproject.service.linea.proveedor.CostMapperByIdFactory;
import devs.mrp.gullproject.service.linea.proveedor.CostRemapperUtilities;
import devs.mrp.gullproject.service.linea.proveedor.SupplierLineMapperByPropAndAssignedLineFactory;
import devs.mrp.gullproject.service.propuesta.oferta.FromPropuestaToOfertaFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.FromPropuestaToProveedorFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.ProposalCostNameMapperFromPvpFactory;
import devs.mrp.gullproject.service.propuesta.proveedor.PropuestaProveedorExtractorNoFlux;
import devs.mrp.gullproject.service.propuesta.proveedor.PropuestaProveedorUtilities;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping(path = "/lineas")
public class AssignLinesInOfferController extends LineaControllerSetup {

	@Autowired SupplierLineMapperByPropAndAssignedLineFactory supplierLineMapperByPropProvIdAndCounterLineId;
	@Autowired PvpMapperByCounterLineFactory<Linea> pvpMapper;
	@Autowired PvpMarginMapperByCounterIdFactory marginMapper;
	@Autowired PvpSumByCounterIdFactory sumMapper;
	@Autowired FromPropuestaToOfertaFactory ofertaConverter;
	@Autowired ProposalCostNameMapperFromPvpFactory costFromPvpMapper;
	@Autowired FromPropuestaToProveedorFactory proveedorFactory;
	@Autowired CostMapperByIdFactory lineCostMapper;
	
	public AssignLinesInOfferController(LineaService lineaService, ConsultaService consultaService,
			AtributoServiceProxyWebClient atributoService, LineaUtilities lineaUtilities,
			AttRemaperUtilities attRemaperUtilities, CostRemapperUtilities costRemapperUtilities,
			PropuestaProveedorUtilities propuestaProveedorUtilities,
			SupplierLineFinderByProposalAssignation finder) {
		super(lineaService, consultaService, atributoService, lineaUtilities, attRemaperUtilities, costRemapperUtilities,
				propuestaProveedorUtilities, finder);
	}
	
	@GetMapping("/allof/ofertaid/{propuestaId}")
	public Mono<String> showAllLinesOfOferta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		return consultaService.findConsultaByPropuestaId(propuestaId)
		.flatMap(rCons -> {
			var ops = rCons.operations();
			PropuestaNuestra propuesta = (PropuestaNuestra)ops.getPropuestaById(propuestaId);
			Propuesta propCliente = ops.getPropuestaById(propuesta.getForProposalId());
			
			model.addAttribute("propuesta", propuesta);
			MyMapperByDupla<Double, Linea, String> sumsMapper = new PvpSumForLineFinder(propuesta);
			model.addAttribute("sumsMapper", sumsMapper);
			model.addAttribute("consulta", rCons);
			model.addAttribute("propCliente", propCliente);
			model.addAttribute("lineasCliente", lineaService.findByPropuestaId(propCliente.getId()));
			
			return supplierLineFinderByProposalAssignation.findBy(propCliente.getId())
				.collectList()
				.map(proveedorLineList -> {
					MyListOfAsignables<Linea> asignablesProveedor = new LineByAssignationRetriever<>(proveedorLineList);
					model.addAttribute("costMapper", new CustomerLineToCostMapper(asignablesProveedor));
					return true;
				})
				.then(lineaService.findByPropuestaId(propuesta.getId()).collectList())
				.map(ofertaLineList -> {
					var pvpMapper = pvpMapperByLineFactory.from(ofertaLineList);
					model.addAttribute("pvpMapper", pvpMapper);
					return true;
				})
				;
		})
		.then(Mono.just("showAllLineasOfOferta"));
	}

	@GetMapping("/allof/ofertaid/{propuestaId}/assign")
	public Mono<String> assignLinesOfOferta(Model model, @PathVariable(name = "propuestaId") String propuestaId) {
		return consultaService.findConsultaByPropuestaId(propuestaId)
				.map(rConsulta -> {
					model.addAttribute("consulta", rConsulta);
					var consultaOps = rConsulta.operations();
					var propuestaNuestra = ofertaConverter.from(consultaOps.getPropuestaById(propuestaId));
					// Supplier lines mapper
					return supplierLineFinderByProposalAssignation.findBy(propuestaNuestra.getForProposalId())
						.collectList().map(proveedorLines -> {
							model.addAttribute("supplierLineMapper", supplierLineMapperByPropProvIdAndCounterLineId.from(proveedorLines));
							model.addAttribute("proposalCostMapperToPVp", costFromPvpMapper.from(propuestaNuestra, consultaOps.getPropuestasProveedorAssignedTo(propuestaNuestra.getForProposalId()).stream().map(p -> proveedorFactory.from(p)).collect(Collectors.toList())));
							model.addAttribute("lineCostMapper", lineCostMapper.from(proveedorLines));
							return null;
						})
					// Offer pvps, sums, margins mappers
						.thenMany(lineaService.findByPropuestaId(propuestaId))
						.collectList().map(offerLines -> {
							model.addAttribute("propuestaNuestra", propuestaNuestra);
							model.addAttribute("pvps", propuestaNuestra.getPvps());
							model.addAttribute("pvpMapper", pvpMapper.from(offerLines));
							model.addAttribute("marginMapper", marginMapper.from(offerLines));
							model.addAttribute("sumMapper", sumMapper.from(propuestaNuestra, offerLines));
							return null;
						})
					// Customer Lines
						
						;
				})
		.then(Mono.just("assignLinesOfOferta"));
	}
	
}
