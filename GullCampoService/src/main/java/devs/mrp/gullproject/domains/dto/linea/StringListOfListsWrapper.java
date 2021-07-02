package devs.mrp.gullproject.domains.dto.linea;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.gullproject.domains.dto.StringListWrapper;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StringListOfListsWrapper {

	List<StringListWrapper> stringListWrapper = new ArrayList<>(); // lineas
	List<String> strings = new ArrayList<>(); // attributes columns
	List<Integer> name = new ArrayList<>(); // line-name components by order
	String nameError;
	String fieldError;
	String costeError;
	
	public void add(StringListWrapper w) {
		stringListWrapper.add(w);
	}
	
	public StringListWrapper getList(int i) {
		return stringListWrapper.get(i);
	}
	
	public void add(String s) {
		strings.add(s);
	}
	
	public String getString(int i) {
		return strings.get(i);
	}
	
	public void addName(Integer i) {
		name.add(i);
	}
	
	public Integer getName(int i) {
		return name.get(i);
	}
	
}
