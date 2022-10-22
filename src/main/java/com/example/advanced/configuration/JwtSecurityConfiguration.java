package com.example.advanced.configuration;

import com.example.advanced.jwt.JwtFilter;
import com.example.advanced.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfiguration
    extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  private final TokenProvider tokenProvider;

  @Override
  public void configure(HttpSecurity httpSecurity) {
    JwtFilter customJwtFilter = new JwtFilter(tokenProvider);
    httpSecurity.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
