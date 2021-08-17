package devs.mrp.gullproject.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthProvider implements AuthenticationProvider {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		
		UserDetails u = userDetailsService.loadUserByUsername(username);
		
		if (u != null && passwordEncoder.matches(password, u.getPassword())) {
			return new UsernamePasswordAuthenticationToken(username, password, u.getAuthorities());
		} else {
			throw new BadCredentialsException("Credenciales incorrectas");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
