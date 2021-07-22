package devs.mrp.gullproject.domains;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.ainterfaces.MyFactoryNew;

@Service
public class ConsultaFactory implements MyFactoryNew<Consulta> {

	@Override
	public Consulta create() {
		return new ConsultaImpl();
	}

}
