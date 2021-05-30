package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class StringListOfListsWrapper {

	List<StringListWrapper> stringListWrapper = new ArrayList<>();
	
}
