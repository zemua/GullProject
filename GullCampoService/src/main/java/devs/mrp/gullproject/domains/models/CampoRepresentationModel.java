package devs.mrp.gullproject.domains.models;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;

@Data
public class CampoRepresentationModel extends RepresentationModel<CampoRepresentationModel> {

	String id;
	String atributoId;
	Object datos; // evitamos genérics para poder hacer el asembler sin problemas
	
}
