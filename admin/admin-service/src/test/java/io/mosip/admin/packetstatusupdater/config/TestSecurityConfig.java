package io.mosip.admin.packetstatusupdater.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class TestSecurityConfig  {

	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}

	@Bean
	protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(cfg -> cfg.disable());
		httpSecurity.authorizeHttpRequests(
				http -> http.anyRequest().permitAll());
		httpSecurity.exceptionHandling(cfg -> cfg.authenticationEntryPoint(unauthorizedEntryPoint()));
		httpSecurity.sessionManagement(cfg -> cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		httpSecurity.httpBasic(Customizer.withDefaults());
		return httpSecurity.build();
	}

	@Bean
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		List<UserDetails> users = new ArrayList<>();
		users.add(new User("reg-officer", "mosip",
				Arrays.asList(new SimpleGrantedAuthority("ROLE_REGISTRATION_OFFICER"))));
		users.add(new User("reg-supervisor", "mosip",
				Arrays.asList(new SimpleGrantedAuthority("ROLE_REGISTRATION_SUPERVISOR"))));
		users.add(new User("reg-admin", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_REGISTRATION_ADMIN"))));
		users.add(new User("reg-processor", "mosip",
				Arrays.asList(new SimpleGrantedAuthority("ROLE_REGISTRATION_PROCESSOR"))));
		users.add(new User("id-auth", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_ID_AUTHENTICATION"))));
		users.add(new User("individual", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_INDIVIDUAL"))));
		users.add(new User("test", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_TEST"))));
		users.add(new User("zonal-admin", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_ZONAL_ADMIN"))));
		users.add(
				new User("zonal-approver", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_ZONAL_APPROVER"))));
		users.add(new User("central-admin", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_CENTRAL_ADMIN"))));
		users.add(new User("device-provider", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_DEVICE_PROVIDER"))));
		users.add(new User("global-admin", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_GLOBAL_ADMIN"))));
		users.add(new User("packet-admin", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_GLOBAL_ADMIN"),
				new SimpleGrantedAuthority("ROLE_DATA_READ"))));
		users.add(new User("digitalcard-admin", "mosip", Arrays.asList(new SimpleGrantedAuthority("ROLE_DIGITALCARD_ADMIN"))));


		return new InMemoryUserDetailsManager(users);
	}
}