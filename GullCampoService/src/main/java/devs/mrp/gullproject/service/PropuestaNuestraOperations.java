package devs.mrp.gullproject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import devs.mrp.gullproject.domains.PropuestaNuestra;
import devs.mrp.gullproject.domains.Pvper;
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
	
}
