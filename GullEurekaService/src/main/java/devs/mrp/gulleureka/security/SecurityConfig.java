package devs.mrp.gulleureka.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${gull.user}")
	private String user;
	
	@Value("${gull.secret}")
	private String secret;
	
	@Bean
	public UserDetailsService uds() {
		var uds = new InMemoryUserDetailsManager();
		var u = User.withUsername(user)
				.password(secret)
				.authorities("system")
				.build();
		uds.createUser(u);
		return uds;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
			.and().authorizeRequests().anyRequest().authenticated()
			.and().httpBasic().and().csrf().disable();
	}
	
}
