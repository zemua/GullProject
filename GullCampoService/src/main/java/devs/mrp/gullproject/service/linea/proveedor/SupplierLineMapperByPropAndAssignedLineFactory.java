package devs.mrp.gullproject.service.linea.proveedor;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;

public interface SupplierLineMapperByPropAndAssignedLineFactory extends MyFactoryFromTo<List<Linea>, MyMapperByDupla<Linea, String, String>> {

}
