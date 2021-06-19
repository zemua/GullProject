package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import devs.mrp.gullproject.domains.dto.CostesCheckbox;
import devs.mrp.gullproject.domains.dto.CostesWrapper;
import devs.mrp.gullproject.domains.dto.IntegerWrap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class PropuestaProveedorOperations {

	private final PropuestaProveedor propuesta;
	
	public PropuestaProveedorOperations(PropuestaProveedor prop) {
		this.propuesta = prop;
	}
	
	public void initializeStandardCosts() {
		CosteProveedor coste = new CosteProveedor();
		coste.setName("COSTE BASE");
		List<CosteProveedor> list = new ArrayList<>();
		list.add(coste);
		propuesta.setCostes(list);
	}
	
	public boolean ifIsCosteProveedorId(String id) {
		Optional<CosteProveedor> cos = propuesta.getCostes().stream().filter(c -> c.getId().equals(id)).findAny();
		return cos.isPresent();
	}
	
	public boolean ifValidCosteValue(String valor) {
		return valor.matches("^[+-]?\\d+[[,\\.]\\d]*$");
	}
	
	public CosteProveedor getCosteByCosteId(String id) {
		Optional<CosteProveedor> cos = propuesta.getCostes().stream().filter(c -> c.getId().equals(id)).findAny();
		return cos.orElse(null);
	}
	
	public static void validateCosts(CostesWrapper costes, BindingResult bindingResult) {
		log.debug("a validar costes: " + costes.toString() + " con binding " + bindingResult.toString());
		for(int i=0; i<costes.getCostes().size(); i++) {
			if (costes.getCostes().get(i).getName() == null || costes.getCostes().get(i).getName().equals("")) {
				bindingResult.rejectValue("costes[" + i + "].name", "error.costes[" + i + "].name");
			}
		}
	}
	
	public List<CostesCheckbox> getCostesCheckbox(ModelMapper modelMapper) {
		return propuesta.getCostes().stream().map(c -> modelMapper.map(c, CostesCheckbox.class)).collect(Collectors.toList());
	}
	
}
