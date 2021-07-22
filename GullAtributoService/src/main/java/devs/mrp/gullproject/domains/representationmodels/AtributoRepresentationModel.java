package devs.mrp.gullproject.domains.representationmodels;

import org.springframework.hateoas.RepresentationModel;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AtributoRepresentationModel extends RepresentationModel<AtributoRepresentationModel> {

	String id;
	String name;
	DataFormat tipo;
	boolean valoresFijos;
	
	public AtributoRepresentationModel(Atributo att) {
		this.id = att.getId();
		this.name = att.getName();
		this.tipo = att.getTipo();
		this.valoresFijos = att.isValoresFijos();
	}
	
}
