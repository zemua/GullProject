package devs.mrp.gullproject.domains.models;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class CampoRepresentationModel extends RepresentationModel<CampoRepresentationModel> {

	String id;
	String atributoId;
	Object datos; // We avoid generics wildcard Campo<?> to be able to make the assembler class without issues
	
}
