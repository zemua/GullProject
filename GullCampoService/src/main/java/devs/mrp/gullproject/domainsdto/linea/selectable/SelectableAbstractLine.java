package devs.mrp.gullproject.domainsdto.linea.selectable;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import devs.mrp.gullproject.ainterfaces.MySelectable;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Document (collection = "lineasoferta")
@NoArgsConstructor
public abstract class SelectableAbstractLine extends LineaAbstracta implements MySelectable {

	private Boolean selected;

	@Override
	public Boolean getSelected() {
		return selected;
	}

	@Override
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	
}
