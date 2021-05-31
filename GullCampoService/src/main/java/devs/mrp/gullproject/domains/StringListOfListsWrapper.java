package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class StringListOfListsWrapper {

	List<StringListWrapper> stringListWrapper = new ArrayList<>();
	List<String> strings = new ArrayList<>();
	
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
