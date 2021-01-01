package devs.mrp.gullproject.domains;

import java.util.List;
import java.util.Map;

public interface Propuesta {

	public void addLinea(Linea linea);
	public void addLineas(List<Linea> lineas);
	public boolean removeLinea(Linea linea);
	public boolean removeLinea(String id);
	public void removeLineas(List<Linea> lineas);
	public List<Linea> getAllLineas();
	public Linea getLinea(int index);
	public boolean updateLinea(String id, Linea linea);
	public boolean updateLinea(int index, Linea linea);
	public boolean saveOrder(Map<Integer, String> map); // Integer = posición, String = id-de-linea
	
	public boolean assertEquals(Linea linea1, Linea linea2);
	public boolean assertEquals(Linea linea1, Linea linea2, List<AtributoForCampo> atributos);
	
}
