package devs.mrp.gullproject.domains;

import java.util.List;
import java.util.Map;

public class OfertaProveedor extends PropuestaAbstracta {

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public boolean isForBid() {
		return false;
	}

}
