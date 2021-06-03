package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StringListWrapper {

	List<String> string;
	String name;
	
	public void add(String s) {
		string.add(s);
	}
	
	public String get(int i) {
		return string.get(i);
	}
	
}
