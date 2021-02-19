package devs.mrp.gullproject.domains;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DataFormat {

	DESCRIPCION(String.class), CANTIDAD(Integer.class), COSTE(Double.class), MARGEN(Float.class), PVP(Double.class), PLAZO(Long.class);
	
	Class<?> clase;
	
	private DataFormat(Class<?> clase) {
		this.clase = clase;
	}
	
	public String getTipo() {
		return this.clase.getSimpleName();
	}
	
	public boolean checkIfValidValue(String s) {
		if (clase.equals(String.class)) {
			return true;
		} else if (clase.equals(Integer.class)) {
			return checkIfInteger(s);
		} else if (clase.equals(Long.class)) {
			return checkIfLong(s);
		} else if (clase.equals(Double.class)) {
			return checkIfDouble(s);
		} else if (clase.equals(Float.class)) {
			return checkIfFloat(s);
		} else if (clase.equals(Boolean.class)) {
			return checkIfBoolean(s);
		}
		return false;
	}
	
	private boolean checkIfInteger(String s) {
		boolean b = false;
		try {
			Integer i = Integer.parseInt(s);
			b = true;
		}
		 catch (Exception e) {
			log.debug("Incorrect value passed when it should be an integer");
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
	private boolean checkIfLong(String s) {
		boolean b = false;
		try {
			Long i = Long.parseLong(s);
			b = true;
		}
		 catch (Exception e) {
			log.debug("Incorrect value passed when it should be a long");
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
	private boolean checkIfDouble(String s) {
		boolean b = false;
		try {
			Double d = Double.parseDouble(s);
			b = true;
		}
		catch (Exception e) {
			log.debug("Incorrect value passed when it should be a double");
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
	private boolean checkIfFloat(String s) {
		boolean b = false;
		try {
			Float d = Float.parseFloat(s);
			b = true;
		}
		catch (Exception e) {
			log.debug("Incorrect value passed when it should be a float");
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
	private boolean checkIfBoolean(String s) {
		boolean b = false;
		try {
			Boolean d = Boolean.parseBoolean(s);
			b = true;
		}
		catch (Exception e) {
			log.debug("Incorrect value passed when it should be a boolean");
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
}
