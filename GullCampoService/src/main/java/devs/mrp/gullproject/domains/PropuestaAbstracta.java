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
	
	List<String> lineaIds = new ArrayList<>();

	@Override
	public abstract boolean isRoot();

	@Override
	public abstract boolean isForBid();

	@Override
	public void addLineaId(String lineaId) {
		lineaIds.add(lineaId);
		
	}

	@Override
	public void addLineaIds(List<String> lineaIds) {
		lineaIds.addAll(lineaIds);
		
	}

	@Override
	public boolean removeLineaId(String lineaId) {
		return lineaIds.remove(lineaId);
	}

	@Override
	public void removeLineaIds(List<String> lineaIds) {
		lineaIds.removeAll(lineaIds);
	}

	@Override
	public List<String> getAllLineaIds() {
		return lineaIds;
	}
	
	@Override
	public int getCantidadLineaIds() {
		return lineaIds.size();
	}

	@Override
	public String getLineaIdByIndex(int index) {
		return lineaIds.get(index);
	}

	@Override
	public boolean updateLineaId(String idOriginal, String idDeseado) {
		ListIterator<String> it = lineaIds.listIterator();
		while (it.hasNext()) {
			String li = it.next();
			if(li.equals(idOriginal)) {
				it.set(idDeseado);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean updateLineaIdByIndex(int index, String lineaId) {
		return lineaIds.set(index, lineaId) != null;
	}

	@Override
	public boolean saveOrder(Map<String, Integer> idlineaVSposicion) {
		lineaIds.stream().forEach(li -> {
			if (!idlineaVSposicion.containsKey(li)) {
				//li.setOrden(-1);
				// TODO call linea repository and set line order to -1
			} else {
				//li.setOrden(idlineaVSposicion.get(li));
				// TODO call linea repository and set line order to idlineaVSposicion.get(li)
			}
		});
		return true;
	}
}
