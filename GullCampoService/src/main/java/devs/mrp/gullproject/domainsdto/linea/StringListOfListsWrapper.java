package devs.mrp.gullproject.domainsdto.linea;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.gullproject.domainsdto.StringListWrapper;
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
	String qtyError;
	
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
	
	public List<Integer> getName() {
		return this.name;
	}
	
	public void setName(List<Integer> names) {
		this.name = names;
	}
	
}
