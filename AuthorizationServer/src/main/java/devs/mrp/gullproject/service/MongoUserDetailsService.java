package devs.mrp.gullproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import devs.mrp.gullproject.models.MyUser;
import devs.mrp.gullproject.repository.UserRepository;

@Component
public class MongoUserDetailsService implements UserDetailsService {

	@Autowired UserRepository repo;
	//@Autowired PasswordEncoder encoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MyUser user = repo.findByUsername(username);
		if (user == null && username != "admin") {
			throw new UsernameNotFoundException("User not found");
		} else if (user == null) {
			return new User("admin", /*encoder.encode("admin")*/"admin", List.of(new SimpleGrantedAuthority("admin")));
		}
		List<SimpleGrantedAuthority> authorities = user.getAuthorities();
		return new User(user.getUsername(), user.getPassword(), authorities);
	}

}
