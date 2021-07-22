package devs.mrp.gullproject.controller.pojos;

import java.util.List;

import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLineImpl;
import lombok.Data;

@Data
public class SelectableLinesWrapPojo {
	
	private List<SelectableLineImpl> lineas;

}
