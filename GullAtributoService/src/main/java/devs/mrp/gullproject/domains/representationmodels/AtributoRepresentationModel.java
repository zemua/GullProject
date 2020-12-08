package devs.mrp.gullproject.domains.representationmodels;

import org.springframework.hateoas.RepresentationModel;

import devs.mrp.gullproject.domains.DataFormat;
import lombok.Data;

@Data
public class AtributoRepresentationModel extends RepresentationModel<AtributoRepresentationModel> {

	String id;
	String name;
	DataFormat tipo;
	boolean valoresFijos;
	
}
