package devs.mrp.gullproject.models.factory;

import org.springframework.stereotype.Component;

import devs.mrp.gullproject.models.MyUser;
import devs.mrp.gullproject.models.MyUserImpl;

@Component
public class UserFactoryImpl implements UserFactory {

	@Override
	public MyUser create() {
		return new MyUserImpl();
	}

	@Override
	public MyUser create(String name) {
		var user = new MyUserImpl();
		user.setUsername(name);
		return user;
	}
	
}
