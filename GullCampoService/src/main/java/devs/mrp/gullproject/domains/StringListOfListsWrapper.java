package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StringListOfListsWrapper {

	List<StringListWrapper> stringListWrapper = new ArrayList<>(); // lineas
	List<String> strings = new ArrayList<>(); // cabecera / columns
	List<Integer> name = new ArrayList<>(); // line-name components by order
	
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
	
}
