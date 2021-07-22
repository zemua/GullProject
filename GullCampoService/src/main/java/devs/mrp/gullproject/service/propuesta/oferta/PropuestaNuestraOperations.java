package devs.mrp.gullproject.service.propuesta.oferta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.domains.propuestas.PvperSum;
import devs.mrp.gullproject.domainsdto.BooleanWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvpOrdenable;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckbox;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckboxedCosts;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperSumCheckbox;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperSumCheckboxWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperSumCheckboxedPvps;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperSumCheckboxedPvpsWrapper;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperSumOrdenable;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvpsCheckboxedCostWrapper;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.propuesta.PropuestaOperations;
import lombok.Data;
import reactor.core.publisher.Mono;

@Data
public class PropuestaNuestraOperations extends PropuestaOperations {

	protected final PropuestaNuestra propuestaNuestra;
	
	public PropuestaNuestraOperations(PropuestaNuestra prop) {
		super(prop);
		propuestaNuestra = (PropuestaNuestra)propuesta;
	}
	
	public boolean ifHasPvp(String pvpId) {
		return propuestaNuestra.getPvps().stream().filter(p -> p.getId().equals(pvpId)).findAny().isPresent();
	}
	
	public Pvper getPvpById(String pvpId) {
		return propuestaNuestra.getPvps().stream().filter(p -> p.getId().equals(pvpId)).findAny().orElse(null);
	}
	
	public boolean ifHasSum(String sumId) {
		return propuestaNuestra.getSums().stream().filter(s -> s.getId().equals(sumId)).findAny().isPresent();
	}
	
	public PvperSum getSumById(String sumId) {
		return propuestaNuestra.getSums().stream().filter(s -> s.getId().equals(sumId)).findAny().orElse(null);
	}
	
	public List<PvperCheckbox> getPvpsCheckbox(ModelMapper modelMapper) {
		return propuestaNuestra.getPvps().stream().map(p -> modelMapper.map(p, PvperCheckbox.class)).collect(Collectors.toList());
	}
	
	public List<PvpOrdenable> getPvpsOrdenables(ModelMapper modelMapper) {
		return propuestaNuestra.getPvps().stream().map(p -> modelMapper.map(p, PvpOrdenable.class)).collect(Collectors.toList());
	}
	
	public static List<Pvper> fromPvpsOrdenablesToPvper(ModelMapper modelMapper, List<PvpOrdenable> pvps) {
		pvps.sort((p1, p2) -> Integer.valueOf(p1.getOrder()).compareTo(p2.getOrder()));
		return pvps.stream().map(p -> modelMapper.map(p, Pvper.class)).collect(Collectors.toList());
	}
	
	public boolean ifPvpHasCost(Pvper pvp, String costId) {
		if (pvp.getIdCostes() == null) {return false;}
		Optional<String> tiene = pvp.getIdCostes().stream().filter(c -> c.equals(costId)).findAny();
		return tiene.isPresent();
	}
	
	public boolean ifPvpCostHasAtt(Pvper pvp, String cotizId, String attId) {
		if (pvp.getIdAttributesByCotiz() == null) {return false;}
		var tiene = pvp.getIdAttributesByCotiz().stream().filter(al -> al.getCotizId().equals(cotizId)).filter(at -> at.getIds().contains(attId)).findAny();
		return tiene.isPresent();
	}
	
	public Mono<PvpsCheckboxedCostWrapper> getPvpsCheckbox(ModelMapper modelMapper, ConsultaService consultaService) {
		PvpsCheckboxedCostWrapper wrapper = new PvpsCheckboxedCostWrapper();
		wrapper.setPvps(new ArrayList<>());
		return consultaService.findConsultaByPropuestaId(propuestaNuestra.getId())
			.map(cons -> {
				var costes = cons.operations().getCostesOfPropuestasProveedorAssignedTo(propuestaNuestra.getForProposalId());
				var cotizaciones = cons.operations().getPropuestasProveedorAssignedTo(propuestaNuestra.getForProposalId());
				var atributos = cons.operations().getAtributosOfPropuestasProveedorAssignedTo(propuestaNuestra.getForProposalId());
				//Map<String, CosteProveedor> map = cons.operations().mapIdToCosteProveedor(); // using costes instead for correct ordering
				propuestaNuestra.getPvps().stream().forEach(pvp -> {
					PvperCheckboxedCosts boxed = new PvperCheckboxedCosts();
					boxed.setId(pvp.getId());
					boxed.setName(pvp.getName());
					boxed.setCosts(new ArrayList<>());
					boxed.setAttributesByCotiz(new ArrayList<>());
					// ADD COSTES TO BOXED
					costes.stream().map(c -> c.getId()).forEach(id -> {
						PvperCheckboxedCosts.CheckboxedCostId coste = new PvperCheckboxedCosts.CheckboxedCostId();
						coste.setId(id);
						if (ifPvpHasCost(pvp, id)) {
							coste.setSelected(true);
						} else {
							coste.setSelected(false);
						}
						boxed.getCosts().add(coste);
					});
					cotizaciones.stream().map(c -> c.getId()).forEach(id -> {
						// ADD ATTRIBUTES TO BOXED
						var lista = new PvperCheckboxedCosts.AttsList();
						lista.setCotizId(id);
						if (!boxed.getAttributesByCotiz().stream().filter(a -> a.getCotizId().equals(id)).findAny().isPresent()) {
							boxed.getAttributesByCotiz().add(lista);
						}
						var pvpAtts = boxed.getAttributesByCotiz().stream().filter(a -> a.getCotizId().equals(id)).findAny().orElse(lista);
						atributos.stream().map(a -> a.getId()).forEach(aid -> {
							PvperCheckboxedCosts.CheckboxedAttId atributo = new PvperCheckboxedCosts.CheckboxedAttId();
							atributo.setId(aid);
							atributo.setSelected(ifPvpCostHasAtt(pvp, id, aid));
							if (pvpAtts.getAtts() == null) {pvpAtts.setAtts(new ArrayList<>());}
							pvpAtts.getAtts().add(atributo);
						});
					});
					wrapper.getPvps().add(boxed);
				});
				return wrapper;
			})
			;
	}
	
	public Mono<PvperCheckboxedCosts> getSinglePvpCheckboxed(ModelMapper modelMapper, ConsultaService consultaService, Pvper pvp) {
		PvperCheckboxedCosts boxed = new PvperCheckboxedCosts();
		boxed.setId(pvp.getId());
		boxed.setName(pvp.getName());
		boxed.setCosts(new ArrayList<>());
		boxed.setAttributesByCotiz(new ArrayList<>());
		
		return consultaService.findConsultaByPropuestaId(propuestaNuestra.getId())
				.map(cons -> {
					var costes = cons.operations().getCostesOfPropuestasProveedorAssignedTo(propuestaNuestra.getForProposalId());
					var cotizaciones = cons.operations().getPropuestasProveedorAssignedTo(propuestaNuestra.getForProposalId());
					var atributos = cons.operations().getAtributosOfPropuestasProveedorAssignedTo(propuestaNuestra.getForProposalId());
					// ADD COSTES TO BOXED
					costes.stream().map(c -> c.getId()).forEach(id -> {
						PvperCheckboxedCosts.CheckboxedCostId coste = new PvperCheckboxedCosts.CheckboxedCostId();
						coste.setId(id);
						if (ifPvpHasCost(pvp, id)) {
							coste.setSelected(true);
						} else {
							coste.setSelected(false);
						}
						boxed.getCosts().add(coste);
					});
					cotizaciones.stream().map(c -> c.getId()).forEach(id -> {
						// ADD ATTRIBUTES TO BOXED
						var lista = new PvperCheckboxedCosts.AttsList();
						lista.setCotizId(id);
						if (!boxed.getAttributesByCotiz().stream().filter(a -> a.getCotizId().equals(id)).findAny().isPresent()) {
							boxed.getAttributesByCotiz().add(lista);
						}
						var pvpAtts = boxed.getAttributesByCotiz().stream().filter(a -> a.getCotizId().equals(id)).findAny().orElse(lista);
						atributos.stream().map(a -> a.getId()).forEach(aid -> {
							PvperCheckboxedCosts.CheckboxedAttId atributo = new PvperCheckboxedCosts.CheckboxedAttId();
							atributo.setId(aid);
							atributo.setSelected(ifPvpCostHasAtt(pvp, id, aid));
							if (pvpAtts.getAtts() == null) {pvpAtts.setAtts(new ArrayList<>());}
							pvpAtts.getAtts().add(atributo);
						});
					});
					return boxed;
				});
	}
	
	public static void validateNamesAndCostsOfCheckboxedWrapper(PvpsCheckboxedCostWrapper wrapper, BindingResult bindingResult) {
		var pvps = wrapper.getPvps();
		for (int i=0; i<pvps.size(); i++) {
			if (pvps.get(i).getName() == null || pvps.get(i).getName().isBlank()) {
				bindingResult.rejectValue("pvps["+i+"].name", "error.pvps["+i+"].name", "El nombre no debe estar en blanco");
				if (!bindingResult.hasFieldErrors("name")) {
					bindingResult.rejectValue("name", "error.name", "Algunos campos tienen nombre no válido");
				}
			}
			var ifHas = pvps.get(i).getCosts().stream().filter(c -> c.isSelected()).findAny();
			if (!ifHas.isPresent()) {
				var costes = pvps.get(i).getCosts();
				for (int j=0; j<costes.size(); j++) {
					bindingResult.rejectValue("pvps["+i+"].costs["+j+"].id", "error.pvps["+i+"].costs["+j+"].id", "Debes escoger al menos 1 coste");
					if (!bindingResult.hasFieldErrors("costes")) {
						bindingResult.rejectValue("costes", "error.costes", "Debes escoger al menos un coste para cada PVP");
					}
				}
			}
		}
	}
	
	public static List<Pvper> fromCheckboxedWrapperToPvps(PvpsCheckboxedCostWrapper wrapper) {
		List<Pvper> list = new ArrayList<>();
		wrapper.getPvps().stream().forEach(p -> {
			Pvper pvp = new Pvper();
			pvp.setIdCostes(new ArrayList<>());
			pvp.setId(p.getId());
			pvp.setName(p.getName());
			p.getCosts().stream().forEach(c -> {
				if (c.isSelected()) {
					pvp.getIdCostes().add(c.getId());
				}
			});
			list.add(pvp);
		});
		return list;
	}
	
	public Map<String, Pvper> mapIdToPvper() {
		return propuestaNuestra.getPvps().stream().collect(Collectors.toMap((pvp) -> pvp.getId(), (pvp) -> pvp));
	}
	
	public List<PvperSumCheckbox> getPvpSumsCheckbox(ModelMapper modelMapper) {
		return propuestaNuestra.getSums().stream().map(p -> modelMapper.map(p, PvperSumCheckbox.class)).collect(Collectors.toList());
	}
	
	public List<PvperSumOrdenable> getPvpSumsOrdenables(ModelMapper modelMapper) {
		return propuestaNuestra.getSums().stream().map(p -> modelMapper.map(p, PvperSumOrdenable.class)).collect(Collectors.toList());
	}
	
	public static List<PvperSum> fromPvperSumOrdernablesToPvperSums(ModelMapper modelMapper, List<PvperSumOrdenable> sums) {
		sums.sort((s1, s2) -> Integer.valueOf(s1.getOrder()).compareTo(s2.getOrder()));
		return sums.stream().map(s -> modelMapper.map(s, PvperSum.class)).collect(Collectors.toList());
	}
	
	public boolean ifPvperSumHasPvp(PvperSum sum, String pvpId) {
		Optional<String> opt = sum.getPvperIds().stream().filter(id -> id.equals(pvpId)).findAny();
		return opt.isPresent();
	}
	
	public Mono<PvperSumCheckboxedPvpsWrapper> getPvpSumCheckboxedPvpsWrapper(ModelMapper modelMapper, ConsultaService consultaService) {
		PvperSumCheckboxedPvpsWrapper wrapper = new PvperSumCheckboxedPvpsWrapper();
		wrapper.setSums(new ArrayList<>());
		return consultaService.findConsultaByPropuestaId(propuestaNuestra.getId())
			.map(cons -> {
				Map<String, Pvper> map = propuestaNuestra.operationsNuestra().mapIdToPvper();
				propuestaNuestra.getSums().stream().forEach(sum -> {
					PvperSumCheckboxedPvps boxed = new PvperSumCheckboxedPvps();
					boxed.setId(sum.getId());
					boxed.setName(sum.getName());
					boxed.setPvperIds(new ArrayList<>());
					map.keySet().stream().forEach(id -> {
						PvperSumCheckboxedPvps.CheckboxedPvperId pvp = new PvperSumCheckboxedPvps.CheckboxedPvperId();
						pvp.setId(id);
						if (ifPvperSumHasPvp(sum, id)) {
							pvp.setSelected(true);
						} else {
							pvp.setSelected(false);
						}
						boxed.getPvperIds().add(pvp);
					});
					wrapper.getSums().add(boxed);
				});
				return wrapper;
			})
			;
	}
	
	public static void validateNamesAndCostsOfSumsCheckboxedWrapper(PvperSumCheckboxedPvpsWrapper wrapper, BindingResult bindingResult) {
		var sums = wrapper.getSums();
		for (int i=0; i<sums.size(); i++) {
			if (sums.get(i).getName() == null || sums.get(i).getName().isBlank()) {
				bindingResult.rejectValue("sums["+i+"].name", "error.sums["+i+"].name", "El nombre no debe estar en blanco");
				if (!bindingResult.hasFieldErrors("name")) {
					bindingResult.rejectValue("name", "error.name", "Algunos campos tienen nombre no válido");
				}
			}
			var ifHas = sums.get(i).getPvperIds().stream().filter(c -> c.isSelected()).findAny();
			if (!ifHas.isPresent()) {
				var pvps = sums.get(i).getPvperIds();
				for (int j=0; j<pvps.size(); j++) {
					bindingResult.rejectValue("sums["+i+"].pvperIds["+j+"].id", "error.sums["+i+"].pvperIds["+j+"].id", "Debes escoger al menos 1 PVP");
					if (!bindingResult.hasFieldErrors("pvps")) {
						bindingResult.rejectValue("pvps", "error.pvps", "Debes escoger al menos un PVP para cada combinado");
					}
				}
			}
		}
	}
	
	public static List<PvperSum> fromSumCheckboxedWrapperToPvps(PvperSumCheckboxedPvpsWrapper wrapper) {
		List<PvperSum> list = new ArrayList<>();
		wrapper.getSums().stream().forEach(p -> {
			PvperSum sum = new PvperSum();
			sum.setPvperIds(new ArrayList<>());
			sum.setId(p.getId());
			sum.setName(p.getName());
			p.getPvperIds().stream().forEach(c -> {
				if (c.isSelected()) {
					sum.getPvperIds().add(c.getId());
				}
			});
			list.add(sum);
		});
		return list;
	}
	
}
