package devs.mrp.gullproject;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class StringContainsMatcher extends BaseMatcher<String> {
	
	String toComp;
	
	public StringContainsMatcher(String toComp){
		this.toComp = toComp;
	}
	
	public void setToComp(String s) {
		this.toComp = s;
	}

	@Override
	public boolean matches(Object actual) {
		if (actual instanceof String) {
		return ((String)actual).contains(toComp);
		}
		return false;
	}

	@Override
	public void describeTo(Description description) {
		
	}
}
