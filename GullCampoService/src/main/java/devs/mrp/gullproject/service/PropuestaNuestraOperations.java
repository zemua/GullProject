package devs.mrp.gullproject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.Pvper;
import devs.mrp.gullproject.domains.dto.PvpOrdenable;
import devs.mrp.gullproject.domains.dto.PvperCheckbox;
import lombok.Data;

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
	
}
