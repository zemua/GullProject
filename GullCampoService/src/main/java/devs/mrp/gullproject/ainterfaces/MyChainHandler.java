package devs.mrp.gullproject.ainterfaces;

public interface MyChainHandler <I, O> {

	public O handleRequest(I input);
	
	public boolean canHandle();
	
	public void setNextHandler(MyChainHandler<I, O> handler);
	
}
