package devs.mrp.gullproject.domains.representationmodels;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import devs.mrp.gullproject.domains.Tipo;
import lombok.Data;

@Data
public class AtributoRepresentationModel extends RepresentationModel<AtributoRepresentationModel> {

	String id;
	String name;
	List<Tipo> tipos;
	boolean valoresFijos;
	
}
