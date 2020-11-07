package devs.mrp.gullproject.domains.representationmodels;

import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MaterialRepresentationModel extends RepresentationModel<MaterialRepresentationModel> {

	String id;
	String name;
	
}
