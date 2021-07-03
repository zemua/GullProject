package devs.mrp.gullproject.service.linea.oferta;

import devs.mrp.gullproject.ainterfaces.MyChainHandler;
import devs.mrp.gullproject.ainterfaces.MyFactoryCopy;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.LineaOferta;

public class LineChainCloneFactory <T extends Linea> implements MyFactoryCopy<Linea>, MyChainHandler<T, T> {

	MyChainHandler<T,T> nextHandler;
	private T data;
	
	@Override
	public T handleRequest(T input) {
		data = input;
		if (!canHandle()) {
			return (T)nextHandler.handleRequest(input);
		}
		return (T)from(input);
	}

	@Override
	public boolean canHandle() {
		if (data instanceof Linea) {
			return true;
		}
		return false;
	}

	@Override
	public void setNextHandler(MyChainHandler<T, T> handler) {
		nextHandler = handler;
	}

	@Override
	public Linea from(Linea element) {
		return new Linea(element);
	}

}
