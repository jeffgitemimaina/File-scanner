package com.velocistech.filescanner01.Security.config;

import com.velocistech.filescanner01.Service.FileService;
import com.velocistech.filescanner01.Service.UserAuthService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.security.config.http.SessionCreationPolicy.ALWAYS;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserAuthService userAuthService;
    private final FileService fileService;
    private  final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    protected void configure(HttpSecurity http) throws Exception{

        String[] staticResources = {
                "/css/**",
                "/images/**",
                "/fonts/**",
                "/scripts/**",
                "/",
                "/error",
                            "/favicon.ico",
                            "/**/*.png",
                            "/**/*.gif",
                            "/**/*.svg",
                            "/**/*.jpg",
                            "/*.html",
                            "/*.css",
                            "/*.js"};
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(staticResources).permitAll()
                .antMatchers("/registerpage/**").permitAll()
                .antMatchers("/index/**").permitAll()
                .antMatchers("*/**").permitAll()
                .antMatchers("/confirmed/**").permitAll()
                .antMatchers("/EmailConfirmed/**").permitAll()
                .antMatchers("/registersuccess/**").permitAll()
                .antMatchers("/registration/**").permitAll()
                .antMatchers("/register/**").permitAll()
                .antMatchers("/previousScannedFiles1/**").hasAuthority("USER")
                .antMatchers("/signing/**").permitAll()
                .antMatchers( "/js/**", "/css/**","/images/**","/index/**","/signing/**","/SigningPage/**")
                .permitAll()
                .antMatchers("/createfile/**").hasAuthority("USER")
                .antMatchers("/upload/**").hasAuthority("USER")
                .antMatchers("/updatefile/**").hasAuthority("USER")
                .antMatchers("*/previousScannedFiles/**").hasAuthority("USER")
                .antMatchers("/query/**").hasAuthority("USER")
                .anyRequest().authenticated().and()
                .formLogin()
                .loginPage("/signing")
                .permitAll()
                .usernameParameter("email")
                .passwordParameter("pass")
                .defaultSuccessUrl("/index")
                .loginProcessingUrl("/login")
                .successForwardUrl("/index")
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling().accessDeniedPage("/403");

        http.formLogin()
                .successHandler(new AuthenticationSuccessHandler() {

                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException, ServletException {

                        System.out.println("Logged user: " + authentication.getName());

                        response.sendRedirect("/index");
                    }
                });

        http
                .sessionManagement()
                .sessionCreationPolicy(ALWAYS)
                .maximumSessions(2)
                .expiredUrl("/signing?invalid-session=true");

    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userAuthService);
        return provider;
    }

@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
}

