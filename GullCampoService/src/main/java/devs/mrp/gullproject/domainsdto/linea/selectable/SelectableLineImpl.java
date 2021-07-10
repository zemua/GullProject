package devs.mrp.gullproject.domainsdto.linea.selectable;

import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SelectableLineImpl extends SelectableAbstractLine {

	public SelectableLineImpl(LineaAbstracta linea) {
		this.setCampos(linea.getCampos());
		this.setCounterLineId(linea.getCounterLineId());
		this.setId(linea.getId());
		this.setNombre(linea.getNombre());
		this.setOrder(linea.getOrder());
		this.setParentId(linea.getParentId());
		this.setPropuestaId(linea.getPropuestaId());
		this.setPvp(linea.getPvp());
		this.setQty(linea.getQty());
		this.setSelected(false);
	}
	
}
