package com.fuse.configuration;

import com.fuse.enums.RoleEnums;
import com.fuse.security.UnAuthorizedEntryPointJwt;
import com.fuse.security.AuthTokenFilter;
import com.fuse.security.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfiguration{
    private final UserDetailServiceImpl userDetailsService;
    private final UnAuthorizedEntryPointJwt unAuthorizedEntryPointJwt;

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-resources/**", "/swagger-resources"
    };

    public SecurityConfiguration(UserDetailServiceImpl userDetailsService, UnAuthorizedEntryPointJwt unAuthorizedEntryPointJwt){
        this.userDetailsService=userDetailsService;
        this.unAuthorizedEntryPointJwt = unAuthorizedEntryPointJwt;

    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unAuthorizedEntryPointJwt))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/sign-in").permitAll()
                                .requestMatchers("/api/admin/**").permitAll()
                                .requestMatchers (SWAGGER_WHITELIST). permitAll()
                                .requestMatchers("/api/user/activate").permitAll()
                                .requestMatchers("/api/user/register").permitAll()
                                .requestMatchers("/api/user/**").hasAnyRole(RoleEnums.USER.name(),RoleEnums.ADMIN.name())
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
