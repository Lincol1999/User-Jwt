package com.userjwtsecurity.demo.config.auth.utils;

import java.security.Key;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class TokenjwtConfig {

    public final static Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public final static String TOKEN_PREFIX = "Bearer ";
    public final static String HEADER_AUTHORIZATION = "Authorization";

}
