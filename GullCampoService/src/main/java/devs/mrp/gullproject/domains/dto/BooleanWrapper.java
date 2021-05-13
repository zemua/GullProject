package devs.mrp.gullproject.domains.dto;

public class BooleanWrapper {

	private boolean b;
	
	public BooleanWrapper(){
		b = true;
	}
	
	public BooleanWrapper(boolean b) {
		this.b = b;
	}
	
	public void set(boolean b) {
		this.b = b;
	}
	
	public boolean get() {
		return this.b;
	}
	
}
