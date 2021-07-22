package devs.mrp.gullproject.domains.models;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.rest.LineaByIdRestController;

@Service
public class LineaRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Linea, LineaRepresentationModel> {
	
	public LineaRepresentationModelAssembler() {
		/**
		 * en el momento de desarrollo no hay soporte automatico de hateoas
		 * para reactor, por lo que debemos hacer un workaround poniendo
		 * una clase nueva para poder construir los enlaces, ya que no podemos
		 * incluir hateoas.web en las dependencias, para hacer uso de
		 * linkTo y methodOn
		 */
		super(LineaByIdRestController.class, LineaRepresentationModel.class);
	}

	@Override
	public LineaRepresentationModel toModel(Linea entity) {
		
		LineaRepresentationModel linea = super.createModelWithId(entity.getId(), entity);
		
		linea.setCampos(entity.getCampos());
		linea.setId(entity.getId());
		linea.setNombre(entity.getNombre());
		
		return linea;
	}
	
	
	
}
