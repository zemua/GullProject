package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class StringListOfListsWrapper {

	List<StringListWrapper> stringListWrapper = new ArrayList<>();
	
	public void add(StringListWrapper w) {
		stringListWrapper.add(w);
	}
	
	public StringListWrapper get(int i) {
		return stringListWrapper.get(i);
	}
	
}
