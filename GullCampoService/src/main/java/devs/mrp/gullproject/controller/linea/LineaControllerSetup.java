package devs.mrp.gullproject.controller.linea;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyFinder;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaFactory;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.AttRemaperUtilities;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.facade.SupplierLineFinderByProposalAssignation;
import devs.mrp.gullproject.service.linea.LineByAssignationRetrieverFactory;
import devs.mrp.gullproject.service.linea.LineaService;
import devs.mrp.gullproject.service.linea.LineaUtilities;
import devs.mrp.gullproject.service.linea.oferta.PvpMapperByAssignedLineFactory;
import devs.mrp.gullproject.service.linea.proveedor.CostRemapperUtilities;
import devs.mrp.gullproject.service.propuesta.proveedor.PropuestaProveedorUtilities;
import reactor.core.publisher.Flux;

@Controller
@RequestMapping(path = "/lineas")
public abstract class LineaControllerSetup {

	protected LineaService lineaService;
	protected ConsultaService consultaService;
	protected AtributoServiceProxyWebClient atributoService;
	protected LineaUtilities lineaUtilities;
	protected AttRemaperUtilities attRemaperUtilities;
	protected CostRemapperUtilities costRemapperUtilities;
	protected PropuestaProveedorUtilities propuestaProveedorUtilities;
	protected MyFactoryFromTo<List<Linea>, MyMapperByDupla<Double, String, String>> pvpMapperByLineFactory;
	protected MyFinder<Flux<Linea>, String> supplierLineFinderByProposalAssignation;
	protected @Autowired LineaFactory lineaFactory;
	protected @Autowired LineByAssignationRetrieverFactory<Linea> lineByAssignationFactory;
	
	@Autowired
	public LineaControllerSetup(LineaService lineaService, ConsultaService consultaService,
			AtributoServiceProxyWebClient atributoService, LineaUtilities lineaUtilities, AttRemaperUtilities attRemaperUtilities,
			CostRemapperUtilities costRemapperUtilities, PropuestaProveedorUtilities propuestaProveedorUtilities,
			PvpMapperByAssignedLineFactory<Linea> pvpMapperByLineFactory, SupplierLineFinderByProposalAssignation finder) {
		this.lineaService = lineaService;
		this.consultaService = consultaService;
		this.atributoService = atributoService;
		this.lineaUtilities = lineaUtilities;
		this.attRemaperUtilities = attRemaperUtilities;
		this.costRemapperUtilities = costRemapperUtilities;
		this.propuestaProveedorUtilities = propuestaProveedorUtilities;
		this.pvpMapperByLineFactory = pvpMapperByLineFactory;
		this.supplierLineFinderByProposalAssignation = finder;
	}
	
}
