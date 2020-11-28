package devs.mrp.gullproject.domains.representationmodels;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.rest.AtributoRestControllerById;

public class AtributoRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Atributo, AtributoRepresentationModel> {
	
	public AtributoRepresentationModelAssembler() {
		/**
		 * en el momento de desarrollo no hay soporte automatico de hateoas
		 * para reactor, por lo que debemos hacer un workaround poniendo
		 * una clase nueva para poder construir los enlaces, ya que no podemos
		 * incluir hateoas.web en las dependencias, para hacer uso de
		 * linkTo y methodOn
		 */
		super(AtributoRestControllerById.class, AtributoRepresentationModel.class);
	}

	@Override
	public AtributoRepresentationModel toModel(Atributo entity) {
		
		/* AtributoRepresentationModel atributo = instantiateModel(entity);
			// en lugar de usar methodOn()
		Method method = ReflectionUtils.findMethod(AtributoRestController.class, "getAtributoById", Mono.class);
		atributo.add(ControllerLinkBuilder.linkTo(method, entity.getId())).withSelfRel(); */
		
		AtributoRepresentationModel atributo = super.createModelWithId(entity.getId(), entity);
		
		atributo.setId(entity.getId());
		atributo.setName(entity.getName());
		atributo.setTipos(entity.getTipos());
		atributo.setValoresFijos(entity.isValoresFijos());
		return atributo;
		
	}

}
