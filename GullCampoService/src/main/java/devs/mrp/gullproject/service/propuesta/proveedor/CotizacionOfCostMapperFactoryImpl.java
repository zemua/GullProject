package devs.mrp.gullproject.service.propuesta.proveedor;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Consulta;

@Service
public class CotizacionOfCostMapperFactoryImpl implements CotizacionOfCostMapperFactory {

	@Override
	public CotizacionOfCostMapper from(Consulta consulta) {
		return new CotizacionOfCostMapperImpl(consulta);
	}

}
