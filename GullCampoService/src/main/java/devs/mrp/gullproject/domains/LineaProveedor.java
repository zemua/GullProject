package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.gullproject.service.LineaProveedorOperations;

public class LineaProveedor extends Linea {

	private List<CosteLineaProveedor> costesProveedor;

	public List<CosteLineaProveedor> getCostesProveedor() {
		return costesProveedor;
	}

	public void setCostesProveedor(List<CosteLineaProveedor> costesProveedor) {
		this.costesProveedor = costesProveedor;
	}
	
	public LineaProveedor() {
		
	}
	
	public LineaProveedor(LineaProveedor lin) {
		super(lin);
		if (lin.getCostesProveedor() != null) {
			this.costesProveedor = new ArrayList<>();
			lin.getCostesProveedor().stream().forEach(c -> this.costesProveedor.add(new CosteLineaProveedor(c)));
		}
	}
	
	public LineaProveedor(Linea lin) {
		super(lin);
		costesProveedor = new ArrayList<>();
	}
	
	public LineaProveedorOperations operations() {
		return new LineaProveedorOperations(this);
	}
	
}
