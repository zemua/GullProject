package devs.mrp.gullproject.service.propuesta.proveedor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domainsdto.IntegerWrap;
import devs.mrp.gullproject.domainsdto.propuesta.proveedor.CosteOrdenable;
import devs.mrp.gullproject.domainsdto.propuesta.proveedor.CostesCheckbox;
import devs.mrp.gullproject.domainsdto.propuesta.proveedor.CostesWrapper;
import devs.mrp.gullproject.service.ClassDestringfier;
import devs.mrp.gullproject.service.propuesta.PropuestaOperations;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class PropuestaProveedorOperations extends PropuestaOperations {
	
	protected final PropuestaProveedor propuestaProveedor;
	
	public PropuestaProveedorOperations(PropuestaProveedor prop) {
		super(prop);
		propuestaProveedor = (PropuestaProveedor)propuesta;
	}
	
	public void initializeStandardCosts() {
		CosteProveedor coste = new CosteProveedor();
		coste.setName("COSTE BASE");
		List<CosteProveedor> list = new ArrayList<>();
		list.add(coste);
		propuestaProveedor.setCostes(list);
	}
	
	public boolean ifIsCosteProveedorId(String id) {
		Optional<CosteProveedor> cos = propuestaProveedor.getCostes().stream().filter(c -> c.getId().equals(id)).findAny();
		return cos.isPresent();
	}
	
	public boolean ifValidCosteValue(String valor) {
		return ClassDestringfier.ifDouble(valor);
	}
	
	public CosteProveedor getCosteByCosteId(String id) {
		Optional<CosteProveedor> cos = propuestaProveedor.getCostes().stream().filter(c -> c.getId().equals(id)).findAny();
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
		return propuestaProveedor.getCostes().stream().map(c -> modelMapper.map(c, CostesCheckbox.class)).collect(Collectors.toList());
	}
	
	public List<CosteOrdenable> getCostesOrdenables(ModelMapper modelMapper) {
		return propuestaProveedor.getCostes().stream().map(c -> modelMapper.map(c, CosteOrdenable.class)).collect(Collectors.toList());
	}
	
	public static List<CosteProveedor> fromCostesOrdenablesToCostesProveedor(ModelMapper modelMapper, List<CosteOrdenable> costes) {
		log.debug("costes antes de ordenar: " + costes.toString());
		costes.sort((c1, c2) -> Integer.valueOf(c1.getOrder()).compareTo(c2.getOrder()));
		log.debug("costes después de ordenar: " + costes.toString());
		return costes.stream().map(c -> modelMapper.map(c, CosteProveedor.class)).collect(Collectors.toList());
	}
	
}
