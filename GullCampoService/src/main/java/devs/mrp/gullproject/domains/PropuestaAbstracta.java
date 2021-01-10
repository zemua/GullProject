package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import devs.mrp.gullproject.service.LineaService;
import lombok.Data;

@Data
public abstract class PropuestaAbstracta implements Propuesta {
	
	@Id
	String id = new ObjectId().toString();
	
	String nombre;
	
	String parentId;
	
	List<String> lineaIds = new ArrayList<>();
	
	@Autowired
	@JsonIgnore
	LineaService lineaService;

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
}
