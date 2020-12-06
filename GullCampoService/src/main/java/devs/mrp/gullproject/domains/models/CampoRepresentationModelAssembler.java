package devs.mrp.gullproject.domains.models;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.rest.CampoByIdRestController;

public class CampoRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Campo<?>, CampoRepresentationModel> {
	
	public CampoRepresentationModelAssembler() {
		/**
		 * en el momento de desarrollo no hay soporte automatico de hateoas
		 * para reactor, por lo que debemos hacer un workaround poniendo
		 * una clase nueva para poder construir los enlaces, ya que no podemos
		 * incluir hateoas.web en las dependencias, para hacer uso de
		 * linkTo y methodOn
		 */
		super(CampoByIdRestController.class, CampoRepresentationModel.class);
	}

	@Override
	public CampoRepresentationModel toModel(Campo<?> entity) {
		
		CampoRepresentationModel campo = super.createModelWithId(entity.getId(), entity);
		
		campo.setId(entity.getId());
		campo.setAtributoId(entity.getAtributoId());
		campo.setDatos(entity.getDatos());
		
		return campo;
	}

	
	
}
