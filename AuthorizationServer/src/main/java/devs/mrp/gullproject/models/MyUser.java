package devs.mrp.gullproject.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyUser {

	@Id
	public String username;
	public String password;
	public List<SimpleGrantedAuthority> authorities;
	
}
