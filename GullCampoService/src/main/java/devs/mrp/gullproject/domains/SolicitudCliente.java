package devs.mrp.gullproject.domains;

public class SolicitudCliente extends PropuestaAbstracta {

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public boolean isForBid() {
		return false;
	}

}
