package devs.mrp.gullproject.service;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class ClassDestringfier {

	public static Object convert(Class<?> targetType, String text) {
	    PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
	    editor.setAsText(text);
	    return editor.getValue();
	}
	
	public static Object toObject( Class<?> clazz, String value ) {
		if (clazz == null) {return toObject(value);}
	    if( Boolean.class == clazz ) return Boolean.parseBoolean( value );
	    if( Byte.class == clazz ) return Byte.parseByte( value );
	    if( Short.class == clazz ) return Short.parseShort( value );
	    if( Integer.class == clazz ) return Integer.parseInt( value );
	    if( Long.class == clazz ) return Long.parseLong( value );
	    if( Float.class == clazz ) return Float.parseFloat( value );
	    if( Double.class == clazz ) return Double.parseDouble( value );
	    if( String.class == clazz ) return value;
	    return toObject(value);
	}
	
	public static Object toObject(String clazz, String value) {
		if (clazz == null) {return toObject(value);}
		if (clazz.equals("String")) return value;
		if (value == null || value.equals("")) {return value;}
		if (clazz.equals("Boolean")) return Boolean.parseBoolean(value);
		if (clazz.equals("Integer")) return Integer.parseInt(value);
		if (clazz.equals("Long")) return Long.parseLong(value);
		if (clazz.equals("Float")) return Float.parseFloat(value);
		if (clazz.equals("Double")) return Double.parseDouble(value);
		return toObject(value);
	}
	
	public static Object toObject(String value) {
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {return Boolean.parseBoolean(value);}
		if (value.matches("^[+-]?\\d{1,9}$")) {return Integer.parseInt(value);}
		if (value.matches("^[+-]?\\d{10,}$")) {return Long.parseLong(value);}
		if (value.matches("^[+-]?\\d+[[,\\.]\\d]*$")) {return Double.parseDouble(value);}
		if (value.matches("^[+-]?\\d+[[,\\.]\\d]*$")) {return Double.parseDouble(value.replace(",", "."));}
		return value;
	}
	
	public static boolean ifDouble(String value) {
		return value.matches("^[+-]?\\d+[[,\\.]\\d]*$");
	}
	
}
