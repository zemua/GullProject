package devs.mrp.gullproject.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import devs.mrp.gullproject.service.MongoUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig /*extends WebSecurityConfigurerAdapter*/ {

	/*@Autowired
	private MongoUserDetailsService uds;*/
	
	@Autowired
	private AuthenticationProvider authProvider;
	
	/* @Bean
	public UserDetailsService uds() {
		return uds;
	} */
	
	@Bean
	UserDetailsService users() {
	    UserDetails user = User.builder()
	      .username("admin")
	      .password("admin")
	      .authorities("admin")
	      .passwordEncoder(s -> s)
	      .build();
	    return new InMemoryUserDetailsManager(user);
	}
	
	/*@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}*/
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.mvcMatchers("/users").hasAuthority("admin")
				.anyRequest().authenticated()
			.and()
				.formLogin().permitAll()
				.and()
				.logout().permitAll()
			;
	}*/
	
	@Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
          authorizeRequests.anyRequest().permitAll()//.authenticated()
        )
          .csrf().disable()
        	.formLogin().permitAll()
          .and()
          .authenticationProvider(authProvider);
        return http.build();
    }
	
}
