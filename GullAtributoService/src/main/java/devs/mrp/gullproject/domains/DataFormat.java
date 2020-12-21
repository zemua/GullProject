package devs.mrp.gullproject.domains;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DataFormat {

	DESCRIPCION(String.class), CANTIDAD(Integer.class), COSTE(Double.class), MARGEN(Float.class), PVP(Double.class), PLAZO(Long.class);
	
	Class<?> clase;
	
	private DataFormat(Class<?> clase) {
		this.clase = clase;
	}
	
	public Class<?> getTipo() {
		return this.clase;
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
			log.debug("se ha pasado un valor incorrecto cuando debía ser un int");
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
			log.debug("se ha pasado un valor incorrecto cuando debía ser un long");
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
			log.debug("Se ha pasado un valor incorrecto cuando debía ser un double");
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
			log.debug("Se ha pasado un valor incorrecto cuando debía ser un float");
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
}
