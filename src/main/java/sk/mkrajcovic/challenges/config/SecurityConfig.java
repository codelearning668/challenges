package sk.mkrajcovic.challenges.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

	private final boolean permitAll;

	SecurityConfig(@Value("${challenges.security.permit-all:false}") boolean permitAll) {
		this.permitAll = permitAll;
	}

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, ServerProperties serverProperties) throws Exception {
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(CsrfConfigurer::disable); // NOSONAR - safe because there is no session
        http.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(FrameOptionsConfig::sameOrigin));

        if (permitAll) {
            LOG.warn("PERMIT ALL - Security turned off!!!");
            http.authorizeHttpRequests(ar -> ar.anyRequest().permitAll());
            return http.build();
        }

        http.exceptionHandling(exh -> exh.authenticationEntryPoint((request, response, authException) -> {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			LOG.error("Not authorized", authException);
        }));

        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            );
		http.httpBasic(Customizer.withDefaults())
			.authorizeHttpRequests(authorize -> authorize
				// we allow all READ operations by default, still @RolesAllowed can restrict this on method level
				.requestMatchers(HttpMethod.GET).permitAll()
				.requestMatchers(
				// not secured
				"/users/register",
				"/ping/**",
				"/swagger-ui/**",
				"/api-docs/**",
				"/openapi/**"
			)
			.permitAll()
			.anyRequest()
			.authenticated());

		return http.build();
    }

	/**
	 * Disabling the Spring's "ROLE_" prefix from GrantedAuthories mappers.<br>
	 * Allows to skip the manual prefix handling when we want to use
	 * <code>@PreAuthorize's "hasAnyRole()"</code> or JSR-250
	 * <code>@RolesAllowed</code> with custom authorities/roles.
	 */
    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

	@Bean
	UserDetailsService userDetailsService(DataSource dataSource) {
		return new JdbcUserDetailsManager(dataSource);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

}
