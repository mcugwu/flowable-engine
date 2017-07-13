package org.flowable.rest.conf.common;

import org.flowable.rest.security.BasicAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new BasicAuthenticationProvider();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider())
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().csrf().disable()
            .authorizeRequests()
            .anyRequest()
            .authenticated().and().httpBasic();
    }
    
    /* Needed for allowing slashes in urls, needed for getting deployment resources */
    @Bean
    public HttpFirewall defaultFireWall() {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.httpFirewall(defaultFireWall());
        super.configure(web);
    }

}
