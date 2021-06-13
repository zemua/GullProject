package devs.mrp.gullproject.domains;

import org.springframework.data.annotation.PersistenceConstructor;

public class PropuestaCliente extends Propuesta {

	public PropuestaCliente() {
		super(TipoPropuesta.CLIENTE);
	}
	
	@PersistenceConstructor
	public PropuestaCliente(TipoPropuesta tipoPropuesta) { // dummy parameter
		super(TipoPropuesta.CLIENTE);
	}

}
