package devs.mrp.gullproject.domains.models;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.rest.ConsultaByIdRestController;

@Service
public class ConsultaRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Consulta, ConsultaRepresentationModel> {

	public ConsultaRepresentationModelAssembler() {
		/**
		 * en el momento de desarrollo no hay soporte automatico de hateoas
		 * para reactor, por lo que debemos hacer un workaround poniendo
		 * una clase nueva para poder construir los enlaces, ya que no podemos
		 * incluir hateoas.web en las dependencias, para hacer uso de
		 * linkTo y methodOn
		 */
		super(ConsultaByIdRestController.class, ConsultaRepresentationModel.class);
	}

	@Override
	public ConsultaRepresentationModel toModel(Consulta entity) {
		
		ConsultaRepresentationModel consulta = super.createModelWithId(entity.getId(), entity);
		
		consulta.setId(entity.getId());
		consulta.setCreatedTime(entity.getCreatedTime());
		consulta.setEditedTime(entity.getEditedTime());
		consulta.setNombre(entity.getNombre());
		consulta.setPropuestas(entity.getPropuestas());
		consulta.setStatus(entity.getStatus());
		
		return consulta;
		
	}	
	
}
