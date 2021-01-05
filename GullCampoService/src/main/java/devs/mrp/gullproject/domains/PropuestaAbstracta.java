package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public abstract class PropuestaAbstracta implements Propuesta {
	
	@Id
	String id = new ObjectId().toString();
	
	String nombre;
	
	String parentId;
	
	List<Linea> lineas = new ArrayList<>();

	@Override
	public abstract boolean isRoot();

	@Override
	public abstract boolean isForBid();

	@Override
	public void addLinea(Linea linea) {
		lineas.add(linea);
		
	}

	@Override
	public void addLineas(List<Linea> lineas) {
		lineas.addAll(lineas);
		
	}

	@Override
	public boolean removeLinea(Linea linea) {
		return lineas.remove(linea);
	}

	@Override
	public boolean removeLinea(String id) {
		return removeLinea(id);
	}

	@Override
	public void removeLineas(List<Linea> lineas) {
		lineas.removeAll(lineas);
	}

	@Override
	public List<Linea> getAllLineas() {
		return lineas;
	}

	@Override
	public Linea getLinea(int index) {
		return lineas.get(index);
	}

	@Override
	public boolean updateLinea(String id, Linea linea) {
		linea.setId(new ObjectId().toString());
		ListIterator<Linea> it = lineas.listIterator();
		while (it.hasNext()) {
			Linea li = it.next();
			if(li.getId().equals(id)) {
				it.set(linea);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean updateLinea(int index, Linea linea) {
		return lineas.set(index, linea) != null;
	}

	@Override
	public boolean saveOrder(Map<String, Integer> idVSposicion) {
		lineas.stream().forEach(li -> {
			if (!idVSposicion.containsKey(li.getId())) {
				li.setOrden(-1);
			} else {
				li.setOrden(idVSposicion.get(li.getId()));
			}
		});
		
		lineas.sort((a, b) -> a.getOrden() - b.getOrden());
		return true;
	}
}
