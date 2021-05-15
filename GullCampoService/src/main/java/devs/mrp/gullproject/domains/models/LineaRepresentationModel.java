package devs.mrp.gullproject.domains.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import devs.mrp.gullproject.domains.Campo;
import lombok.Data;

@Data
public class LineaRepresentationModel extends RepresentationModel<LineaRepresentationModel> {

	private String id;
	private String nombre;
	private List<Campo<?>> campos = new ArrayList<>();
	
	public void resetCampos(List<Campo<Object>> campos) {
		this.campos.clear();
		this.campos.addAll(campos);
	}
	
}
