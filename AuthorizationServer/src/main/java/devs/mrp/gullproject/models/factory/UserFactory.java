package devs.mrp.gullproject.models.factory;

import devs.mrp.gullproject.models.MyUser;

public interface UserFactory {

	public MyUser create();
	
	public MyUser create(String name);
	
}
