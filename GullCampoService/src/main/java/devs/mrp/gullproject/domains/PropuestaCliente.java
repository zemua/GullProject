package devs.mrp.gullproject.domains;

public class PropuestaCliente extends PropuestaAbstracta {

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public boolean isForBid() {
		return false;
	}

}
