package com.timely.projectservice.config;

import com.timely.projectservice.constant.ProfilesConstants;
import com.timely.projectservice.filter.FirebaseFilter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Profile({ ProfilesConstants.PROD, ProfilesConstants.DEV, ProfilesConstants.QA })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final FirebaseFilter firebaseFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()

                .authorizeRequests().anyRequest().permitAll().and()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(firebaseFilter, UsernamePasswordAuthenticationFilter.class);

    }

}
