package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.afactories.LineToProveedorLineFactory;
import devs.mrp.gullproject.ainterfaces.MyConverter;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.LineaProveedor;

@Service
public class LineListToProveedorConverter implements MyConverter<List<Linea>, List<LineaProveedor>> {

	@Autowired LineToProveedorLineFactory factory;
	
	@Override
	public List<LineaProveedor> convert(List<Linea> input) {
		return input.stream().map(l -> factory.from(l)).collect(Collectors.toList());
	}
	
}
