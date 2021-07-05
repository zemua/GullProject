package devs.mrp.gullproject.domainsdto;

import devs.mrp.gullproject.domains.models.ConsultaRepresentationModel;
import lombok.Data;

@Data
public class ConsultaForSpreadsheet {

	String name;
	String status;
	String id;
	
	public static ConsultaForSpreadsheet fromModel(ConsultaRepresentationModel model) {
		ConsultaForSpreadsheet cfs = new ConsultaForSpreadsheet();
		cfs.setName(model.getNombre());
		cfs.setStatus(model.getStatus());
		cfs.setId(model.getId());
		return cfs;
	}
	
}
