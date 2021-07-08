package devs.mrp.gullproject.domainsdto.linea.selectable;

import java.util.List;

import lombok.Data;

public class SelectableLinesWrapImpl implements SelectableLinesWrap {
	
	private List<SelectableAbstractLine> lineas;
	
	@Override
	public List<SelectableAbstractLine> getLineas() {
		return this.lineas;
	}

	@Override
	public void setLineas(List<SelectableAbstractLine> lineas) {
		this.lineas = lineas;
	}

}
