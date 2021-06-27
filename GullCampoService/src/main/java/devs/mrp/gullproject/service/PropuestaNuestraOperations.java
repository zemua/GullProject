package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.Pvper;
import devs.mrp.gullproject.domains.dto.BooleanWrapper;
import devs.mrp.gullproject.domains.dto.PvpOrdenable;
import devs.mrp.gullproject.domains.dto.PvperCheckbox;
import devs.mrp.gullproject.domains.dto.PvperCheckboxedCosts;
import devs.mrp.gullproject.domains.dto.PvpsCheckboxedCostWrapper;
import lombok.Data;
import reactor.core.publisher.Mono;

@Data
public class PropuestaNuestraOperations extends PropuestaOperations {

	protected final PropuestaNuestra propuestaNuestra;
	
	public PropuestaNuestraOperations(PropuestaNuestra prop) {
		super(prop);
		propuestaNuestra = (PropuestaNuestra)propuesta;
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
		Optional<String> tiene = pvp.getIdCostes().stream().filter(c -> c.equals(costId)).findAny();
		return tiene.isPresent();
	}
	
	public Mono<PvpsCheckboxedCostWrapper> getPvpsCheckbox(ModelMapper modelMapper, ConsultaService consultaService) { // TODO test
		PvpsCheckboxedCostWrapper wrapper = new PvpsCheckboxedCostWrapper();
		wrapper.setPvps(new ArrayList<>());
		return consultaService.findConsultaByPropuestaId(propuestaNuestra.getId())
			.map(cons -> {
				Map<String, CosteProveedor> map = cons.operations().mapIdToCosteProveedor();
				propuestaNuestra.getPvps().stream().forEach(pvp -> {
					PvperCheckboxedCosts boxed = new PvperCheckboxedCosts();
					boxed.setId(pvp.getId());
					boxed.setName(pvp.getName());
					boxed.setCosts(new ArrayList<>());
					map.keySet().stream().forEach(id -> {
						PvperCheckboxedCosts.CheckboxedCostId coste = new PvperCheckboxedCosts.CheckboxedCostId();
						coste.setId(id);
						if (ifPvpHasCost(pvp, id)) {
							coste.setSelected(true);
						} else {
							coste.setSelected(false);
						}
						boxed.getCosts().add(coste);
					});
					wrapper.getPvps().add(boxed);
				});
				return wrapper;
			})
			;
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
	
}
