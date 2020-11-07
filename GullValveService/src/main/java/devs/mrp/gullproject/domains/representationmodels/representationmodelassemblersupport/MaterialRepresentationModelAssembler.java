package devs.mrp.gullproject.domains.representationmodels.representationmodelassemblersupport;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.domains.representationmodels.MaterialRepresentationModel;
import devs.mrp.gullproject.rest.MaterialByIdRestController;
import devs.mrp.gullproject.rest.MaterialRestController;

@Service
public class MaterialRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Material, MaterialRepresentationModel> {

	public MaterialRepresentationModelAssembler() {
		super(MaterialByIdRestController.class, MaterialRepresentationModel.class);
	}

	@Override
	public MaterialRepresentationModel toModel(Material entity) {
		MaterialRepresentationModel r = super.createModelWithId(entity.getId(), entity);
		r.setId(entity.getId());
		r.setName(entity.getName());
		return r;
	}
	
	
	
}
