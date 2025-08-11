package com.ramendirectory.japanramendirectory.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;


@Configuration
public class SecurityConfig {
	private AuthUserService userDetailsService;
	private final RsaKeyProperties rsaKeys;
	
	
	@Autowired
	public SecurityConfig( AuthUserService userDetailsService,
			RsaKeyProperties rsaKeys) {
		super();
		this.userDetailsService = userDetailsService;
		this.rsaKeys = rsaKeys;
	}

	@Bean
	JwtDecoder jwtDecoder() {
	    return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
	}
	
	@Bean
	JwtEncoder jwtEncoder() {
	    JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
	    JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
	    return new NimbusJwtEncoder(jwks);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		System.out.println(new BCryptPasswordEncoder().encode("password"));
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {        
        http
        	.cors(cors->cors.configurationSource(corsConfigurationSource()))
        	.csrf(csrf -> csrf.disable())
        	.authorizeHttpRequests(authz -> authz
        			.requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
        			.requestMatchers(HttpMethod.GET, "/api/restaurants").permitAll()
        			.requestMatchers(HttpMethod.GET, "/api/comments/restaurant/*/latest-photo").permitAll()
        			.requestMatchers(HttpMethod.GET, "/api/comments/restaurant/*/*").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/restaurants/*").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/restaurants/genres").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/restaurants/soupbases").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/ranking").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/recaptcha/sitekey").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/comments/restaurant/*").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/address/prefectures").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/ranking").permitAll()
        			.requestMatchers("/auth/login").permitAll()
        			.requestMatchers("/adminonly").hasAuthority("SCOPE_ADMIN")
        			.anyRequest().authenticated()
        	)
//        	.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)  //The old syntax
        	.oauth2ResourceServer(server->server.jwt(Customizer.withDefaults()))
        	.sessionManagement(session -> session
        			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        	)
        	.httpBasic(Customizer.withDefaults());
        return http.build();
	}
	
	

	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // this is the React/frontend port, not
																					// the java port
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("*"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;

	}

	
}
